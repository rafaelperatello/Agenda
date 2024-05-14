package br.edu.ifspsaocarlos.agenda.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.edu.ifspsaocarlos.agenda.R
import br.edu.ifspsaocarlos.agenda.activity.ui.theme.ContentProviderPhonebookTheme
import br.edu.ifspsaocarlos.agenda.data.ContactDAO
import br.edu.ifspsaocarlos.agenda.model.Contact
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivityCompose : ComponentActivity() {

    private val contactDAO: ContactDAO by lazy { ContactDAO(this.applicationContext) }
    private val contactList = mutableStateOf<List<Contact>>(emptyList())

    private val contactSearchQueryStateFlow = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val contactSearchResultStateFlow = MutableStateFlow(emptyList<Contact>())

    private val showSearchBarStateFlow = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ContentProviderPhonebookTheme {
                MainScaffold(
                    contactList = contactList.value,
                    contactSearchQuerySharedFlow = contactSearchQueryStateFlow,
                    contactSearchResultStateFlow = contactSearchResultStateFlow,
                    showSearchBarStateFlow = showSearchBarStateFlow,
                    onFabClick = { showNewContact(this) },
                    onContactClick = { showAddContact(this, it) },
                    onRemoveContactConfirmed = {
                        removeContact(it)
                        if (showSearchBarStateFlow.value) {
                            contactSearchQueryStateFlow.tryEmit(contactSearchQueryStateFlow.replayCache.firstOrNull() ?: "")
                        }
                    }
                )
            }
        }

        refreshDataWhenReady()
        listenToSearchTokenChanges()
        listenSearchBarVisibilityChanges()
    }

    private fun listenSearchBarVisibilityChanges() {
        lifecycleScope.launch {
            showSearchBarStateFlow.collectLatest {
                if (!it) {
                    contactSearchQueryStateFlow.tryEmit("")
                }
            }
        }
    }

    private fun listenToSearchTokenChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                contactSearchQueryStateFlow.collectLatest {
                    if (it.isBlank()) {
                        contactSearchResultStateFlow.value = emptyList()
                    } else {
                        contactSearchResultStateFlow.value = contactDAO.searchContactSuspend(it)
                    }
                }
            }
        }
    }

    private fun refreshDataWhenReady() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                refreshContactList()
            }
        }
    }

    private fun showNewContact(context: Context) {
        val intent = Intent(context, DetailActivity::class.java)
        context.startActivity(intent)
    }

    private fun showAddContact(context: Context, contact: Contact) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("contact", contact) // Todo change to const
        context.startActivity(intent)
    }

    private fun removeContact(contact: Contact) {
        lifecycleScope.launch {
            contactDAO.deleteContactSuspend(contact)
            refreshContactList()
        }
    }

    private suspend fun refreshContactList() {
        contactList.value = contactDAO.searchAllContactsSuspend()
    }
}

@Composable
fun MainScaffold(
    contactList: List<Contact>,
    contactSearchQuerySharedFlow: MutableSharedFlow<String>,
    contactSearchResultStateFlow: StateFlow<List<Contact>>,
    showSearchBarStateFlow: MutableStateFlow<Boolean>,
    onFabClick: () -> Unit = {},
    onContactClick: (Contact) -> Unit = {},
    onRemoveContactConfirmed: (Contact) -> Unit = {}
) {
    Scaffold(
        topBar = {
            val showSearchBar by showSearchBarStateFlow.collectAsStateWithLifecycle()
            val query by contactSearchQuerySharedFlow.collectAsStateWithLifecycle("")

            MainToolBar(
                showSearchBar = showSearchBar,
                query = query,
                onSearchClicked = { showSearchBarStateFlow.value = true },
                onQueryChanged = { contactSearchQuerySharedFlow.tryEmit(it) },
                onSearchClosed = { showSearchBarStateFlow.value = false },
                contactSearchResultStateFlow = contactSearchResultStateFlow,
                onContactClick = onContactClick,
                onRemoveContactConfirmed = onRemoveContactConfirmed
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = { innerPadding ->
            MainBody(
                contactList = contactList,
                modifier = Modifier.padding(innerPadding),
                onItemClick = onContactClick,
                onRemoveContactConfirmed = onRemoveContactConfirmed
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainToolBar(
    showSearchBar: Boolean,
    query: String,
    onSearchClicked: () -> Unit,
    onSearchClosed: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onContactClick: (Contact) -> Unit = {},
    onRemoveContactConfirmed: (Contact) -> Unit = {},
    contactSearchResultStateFlow: StateFlow<List<Contact>>
) {
    if (showSearchBar) {
        val contactSearchResult by contactSearchResultStateFlow.collectAsStateWithLifecycle()

        SearchBar(
            query = query,
            onQueryChange = onQueryChanged,
            onSearch = {},
            active = true,
            onActiveChange = { if (!it) onSearchClosed() },
            placeholder = { Text(stringResource(id = R.string.search_hint)) },
            leadingIcon = {
                IconButton(onClick = onSearchClosed) {
                    Icon(Icons.Filled.Close, null)
                }
            }
        ) {
            if (contactSearchResult.isEmpty() && query.isNotBlank()) {
                Text(
                    text = stringResource(id = R.string.search_empty_results),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                MainBody(
                    contactList = contactSearchResult,
                    onItemClick = onContactClick,
                    onRemoveContactConfirmed = onRemoveContactConfirmed
                )
            }
        }
    } else {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
            actions = {
                IconButton(onClick = onSearchClicked) {
                    Icon(Icons.Filled.Search, null)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainBody(
    contactList: List<Contact>,
    modifier: Modifier = Modifier,
    onItemClick: (Contact) -> Unit = {},
    onRemoveContactConfirmed: (Contact) -> Unit = {}
) {
    val currentContact = remember { mutableStateOf<Contact?>(null) }
    if (currentContact.value != null) {
        AlertDialog(
            onDismissRequest = {
                currentContact.value = null
            },
            title = {
                Text(text = stringResource(id = R.string.remove_contact))
            },
            text = {
                Text(
                    stringResource(
                        id = R.string.remove_contact_confirmation,
                        currentContact.value?.name?.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.empty_name))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentContact.value?.let {
                            onRemoveContactConfirmed(it)
                        }
                        currentContact.value = null
                    }) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        currentContact.value = null
                    }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = contactList,
            key = { _, item -> item }
        ) { _, item ->
            ListItem(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onItemClick(item)
                    },
                    onLongClick = {
                        // Todo
                    }
                ),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                headlineContent = {
                    Text(
                        text = item.name?.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.empty_name),
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                supportingContent = {
                    Text(text = item.phone?.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.empty_phone))
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            currentContact.value = item
                        }
                    )
                }
            )
        }
    }
}

// region Previews
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ContentProviderPhonebookTheme {
        MainScaffold(
            contactList = listOf(
                Contact(1, "Teste", "123456789"),
                Contact(2, null, "987654321"),
                Contact(3, "Teste 3", ""),
            ),
            contactSearchQuerySharedFlow = MutableStateFlow(""),
            contactSearchResultStateFlow = MutableStateFlow(emptyList()),
            showSearchBarStateFlow = MutableStateFlow(false)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToolBarSearchPreview() {
    MainToolBar(
        showSearchBar = true,
        query = "",
        onSearchClicked = {},
        onQueryChanged = {},
        onSearchClosed = {},
        contactSearchResultStateFlow = MutableStateFlow(
            listOf(
                Contact(1, "Test", "123456789"),
                Contact(2, null, "987654321"),
                Contact(3, "Test 3", ""),
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ToolBarEmptySearchPreview() {
    MainToolBar(
        showSearchBar = true,
        query = "Query",
        onSearchClicked = {},
        onQueryChanged = {},
        onSearchClosed = {},
        contactSearchResultStateFlow = MutableStateFlow(emptyList())
    )
}

@Preview(showBackground = true)
@Composable
fun ToolBarPreview() {
    MainToolBar(
        showSearchBar = false,
        query = "",
        onSearchClicked = {},
        onQueryChanged = {},
        onSearchClosed = {},
        contactSearchResultStateFlow = MutableStateFlow(emptyList())
    )
}
// endregion