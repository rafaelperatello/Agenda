package br.edu.ifspsaocarlos.agenda.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifspsaocarlos.agenda.data.ContactDao
import br.edu.ifspsaocarlos.agenda.data.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val contactDao: ContactDao
) : ViewModel() {

    private var refreshJobContacts: Job = Job()

    private val _contactList = MutableStateFlow<List<Contact>>(emptyList())
    val contactListStateFlow = _contactList.asStateFlow()

    private val _showSearchBarStateFlow = MutableStateFlow(false)
    val showSearchBarStateFlow = _showSearchBarStateFlow.asStateFlow()

    private val _searchQueryStateFlow = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val searchQueryStateFlow = _searchQueryStateFlow.asSharedFlow()

    private val _searchResultListStateFlow = MutableStateFlow<List<Contact>>(emptyList())
    val searchResultListStateFlow = _searchResultListStateFlow.asStateFlow()

    init {
        listenSearchTokenChanges()
    }

    fun onSearchClicked() {
        _showSearchBarStateFlow.value = true
    }

    fun onSearchClosed() {
        _showSearchBarStateFlow.value = false
        _searchQueryStateFlow.tryEmit("")
    }

    fun onSearchQueryChanged(query: String) {
        _searchQueryStateFlow.tryEmit(query)
    }

    fun saveNewContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.createContact(contact)
            refreshContactList()
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.updateContact(contact)
            refreshContactList()
            maybeRefreshSearch()
        }
    }

    fun removeContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
            refreshContactList()
            maybeRefreshSearch()
        }
    }

    private suspend fun refreshContactList() {
        refreshJobContacts.cancel()
        refreshJobContacts = viewModelScope.launch {
            _contactList.value = contactDao.searchAllContacts()
        }
    }

    private fun maybeRefreshSearch() {
        if (showSearchBarStateFlow.value) {
            _searchQueryStateFlow.tryEmit(searchQueryStateFlow.replayCache.firstOrNull() ?: "")
        }
    }

    @OptIn(FlowPreview::class)
    private fun listenSearchTokenChanges() {
        viewModelScope.launch {
            _searchQueryStateFlow
                .debounce(150L)
                .collectLatest {
                    if (it.isBlank()) {
                        _searchResultListStateFlow.value = emptyList()
                    } else {
                        _searchResultListStateFlow.value = contactDao.searchContacts(it)
                    }
                }
        }
    }
}