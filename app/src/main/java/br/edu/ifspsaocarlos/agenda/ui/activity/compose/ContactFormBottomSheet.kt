package br.edu.ifspsaocarlos.agenda.ui.activity.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.edu.ifspsaocarlos.agenda.R
import br.edu.ifspsaocarlos.agenda.data.model.Contact
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormBottomSheet(
    contact: Contact? = null,
    onDismissRequest: () -> Unit,
    onSaveContactClicked: (Contact) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                modalBottomSheetState.hide()
                onDismissRequest()
            }
        },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        windowInsets = WindowInsets.ime,
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, bottomPadding)
        ) {

            var nameIsInvalid by rememberSaveable { mutableStateOf(false) }

            var name by rememberSaveable { mutableStateOf(contact?.name ?: "") }
            var phone by rememberSaveable { mutableStateOf(contact?.phone ?: "") }
            var phone2 by rememberSaveable { mutableStateOf(contact?.phone2 ?: "") }
            var email by rememberSaveable { mutableStateOf(contact?.email ?: "") }
            var birthday by rememberSaveable { mutableStateOf(contact?.birthday ?: "") }

            val titleRes = if (contact != null) R.string.edit_contact else R.string.new_contact
            Text(
                text = stringResource(id = titleRes),
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            CustomOutlineText(
                text = name,
                label = stringResource(id = R.string.name),
                maxSize = 50,
                supportText = if (nameIsInvalid) stringResource(id = R.string.name_required) else null,
                isError = nameIsInvalid,
                leadingIcon = Icons.Default.Person,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                onValueChange = {
                    name = it
                    nameIsInvalid = it.isBlank()
                }
            )

            CustomOutlineText(
                text = phone,
                label = stringResource(id = R.string.phone_1),
                maxSize = 20,
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                onValueChange = { phone = it }
            )

            CustomOutlineText(
                text = phone2,
                label = stringResource(id = R.string.phone_2),
                maxSize = 20,
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                onValueChange = { phone2 = it }
            )

            CustomOutlineText(
                text = email,
                label = stringResource(id = R.string.email),
                maxSize = 50,
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                onValueChange = { email = it }
            )

            CustomOutlineText(
                text = birthday,
                label = stringResource(id = R.string.birthday),
                maxSize = 8,
                leadingIcon = Icons.Default.DateRange,
                visualTransformation = DateTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onValueChange = { birthday = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                FilledTonalButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = onDismissRequest,
                ) {
                    Text(stringResource(id = R.string.cancel))
                }

                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        if (name.isBlank()) {
                            nameIsInvalid = true
                            return@Button
                        }

                        val newContact = Contact(
                            id = contact?.id ?: 0L,
                            name = name,
                            phone = phone,
                            phone2 = phone2,
                            email = email,
                            birthday = birthday,
                        )

                        onSaveContactClicked(newContact)
                    },
                ) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun CustomOutlineText(
    text: String,
    label: String,
    maxSize: Int = Int.MAX_VALUE,
    supportText: String? = null,
    isError: Boolean = false,
    leadingIcon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit = {},
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val columnCoroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged {
                if (it.hasFocus) {
                    columnCoroutineScope.launch {
                        // This sends a request to all parents that asks them to scroll so
                        // that this item is brought into view.
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        supportingText = { supportText?.let { Text(it) } },
        isError = isError,
        value = text,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
            )
        },
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        onValueChange = {
            if (it.length <= maxSize) {
                onValueChange(it)
            }
        }
    )
}

private class DateTransformation() : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }

    private fun maskFilter(text: AnnotatedString): TransformedText {
        val trimmed = text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when (offset) {
                    in 0..1 -> offset
                    in 2..3 -> offset + 1
                    else -> offset + 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    in 0..1 -> offset
                    in 1..3 -> offset - 1
                    else -> offset - 2
                }
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}