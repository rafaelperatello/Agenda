package br.edu.ifspsaocarlos.agenda.activity

import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import br.edu.ifspsaocarlos.agenda.activity.compose.ContactFormBottomSheet
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
                var contact by rememberSaveable { mutableStateOf<Contact?>(null) }
                var showSheet by rememberSaveable { mutableStateOf(false) }
                if (showSheet) {
                    ContactFormBottomSheet(
                        contact = contact,
                        onDismissRequest = {
                            showSheet = false
                            contact = null
                        },
                        onSaveContactClicked = {
                            if (contact == null) {
                                saveNewContact(it)
                            } else {
                                updateContact(it)
                            }
                            showSheet = false
                            contact = null
                        },
                    )
                }

                MainScaffold(
                    contactList = contactList.value,
                    contactSearchQuerySharedFlow = contactSearchQueryStateFlow,
                    contactSearchResultStateFlow = contactSearchResultStateFlow,
                    showSearchBarStateFlow = showSearchBarStateFlow,
                    onCheckClicked = {
                        showContentProviderActivity()
                    },
                    onFabClick = {
                        contact = null
                        showSheet = true
                    },
                    onContactClick = {
                        contact = it
                        showSheet = true
                    },
                    onRemoveContactConfirmed = {
                        removeContact(it)
                    }
                )
            }
        }

        refreshDataWhenReady()
        listenSearchTokenChanges()
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

    private fun listenSearchTokenChanges() {
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

    private fun saveNewContact(contact: Contact) {
        lifecycleScope.launch {
            contactDAO.createContactSuspend(contact)
            refreshContactList()
        }
    }

    private fun updateContact(contact: Contact) {
        lifecycleScope.launch {
            contactDAO.updateContactSuspend(contact)
            refreshContactList()
            maybeRefreshSearch()
        }
    }

    private fun removeContact(contact: Contact) {
        lifecycleScope.launch {
            contactDAO.deleteContactSuspend(contact)
            refreshContactList()
            maybeRefreshSearch()
        }
    }

    private suspend fun refreshContactList() {
        contactList.value = contactDAO.searchAllContactsSuspend()
    }

    private fun maybeRefreshSearch() {
        if (showSearchBarStateFlow.value) {
            contactSearchQueryStateFlow.tryEmit(contactSearchQueryStateFlow.replayCache.firstOrNull() ?: "")
        }
    }

    private fun showContentProviderActivity() {
        Intent(this, ContentProviderActivity::class.java).also {
            startActivity(it)
        }
    }
}

@Composable
fun MainScaffold(
    contactList: List<Contact>,
    contactSearchQuerySharedFlow: MutableSharedFlow<String>,
    contactSearchResultStateFlow: StateFlow<List<Contact>>,
    showSearchBarStateFlow: MutableStateFlow<Boolean>,
    onCheckClicked: () -> Unit,
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
                onCheckClicked = onCheckClicked,
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
            val configuration = LocalConfiguration.current
            val modifier = when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> Modifier.safeDrawingPadding()
                else -> Modifier
            }

            FloatingActionButton(
                modifier = modifier,
                onClick = onFabClick
            ) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = { innerPadding ->
            MainContent(
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
    onCheckClicked: () -> Unit,
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
                MainContent(
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
                IconButton(onClick = onCheckClicked) {
                    Icon(Icons.Filled.Check, null)
                }
                IconButton(onClick = onSearchClicked) {
                    Icon(Icons.Filled.Search, null)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(
    contactList: List<Contact>,
    modifier: Modifier = Modifier,
    onItemClick: (Contact) -> Unit = {},
    onRemoveContactConfirmed: (Contact) -> Unit = {}
) {
    val currentContact = rememberSaveable { mutableStateOf<Contact?>(null) }
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
                        currentContact.value?.name?.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.empty_name)
                    )
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
            showSearchBarStateFlow = MutableStateFlow(false),
            onCheckClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToolBarSearchPreview() {
    MainToolBar(
        showSearchBar = true,
        query = "",
        onCheckClicked = {},
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
        onCheckClicked = {},
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
        onCheckClicked = {},
        query = "",
        onSearchClicked = {},
        onQueryChanged = {},
        onSearchClosed = {},
        contactSearchResultStateFlow = MutableStateFlow(emptyList())
    )
}
// endregion