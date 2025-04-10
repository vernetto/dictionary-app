/**
 * Dictionary Management Application JavaScript
 * Handles DataTables initialization and CRUD operations
 */

// Global variables
let dictionaryTable;
let currentDictionary = null;
let selectedEntries = [];

// Initialize the application when document is ready
$(document).ready(function() {
    // Initialize DataTable with empty data
    initializeDataTable();
    
    // Set up event handlers
    setupEventHandlers();
});

/**
 * Initialize DataTable with configuration for sorting, filtering, and pagination
 */
function initializeDataTable() {
    dictionaryTable = $('#dictionaryTable').DataTable({
        columns: [
            { 
                data: null,
                orderable: false,
                className: 'select-checkbox',
                defaultContent: '',
                render: function (data, type, row, meta) {
                    return '<input type="checkbox" class="entry-checkbox" data-index="' + meta.row + '">';
                }
            },
            { data: 'from', title: 'From' },
            { data: 'to', title: 'To' },
            { 
                data: 'known', 
                title: 'Known',
                render: function(data) {
                    return data ? '<span class="badge bg-success">Yes</span>' : '<span class="badge bg-secondary">No</span>';
                }
            },
            { data: 'freq', title: 'Frequency' },
            {
                data: null,
                orderable: false,
                className: 'action-buttons',
                defaultContent: '',
                render: function (data, type, row, meta) {
                    return '<div class="action-buttons">' +
                           '<button class="btn btn-sm btn-primary edit-btn" data-index="' + meta.row + '"><i class="fas fa-edit"></i></button>' +
                           '<button class="btn btn-sm btn-danger delete-btn" data-index="' + meta.row + '"><i class="fas fa-trash"></i></button>' +
                           '</div>';
                }
            }
        ],
        data: [],
        responsive: true,
        dom: 'Bfrtip',
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        pageLength: 10,
        language: {
            emptyTable: "No dictionary selected. Please select a dictionary from the dropdown."
        }
    });
}

/**
 * Set up all event handlers for the application
 */
function setupEventHandlers() {
    // Dictionary selection change
    $('#dictionarySelect').change(function() {
        const selectedFile = $(this).val();
        if (selectedFile) {
            loadDictionary(selectedFile);
        } else {
            // Clear table if no dictionary is selected
            dictionaryTable.clear().draw();
            currentDictionary = null;
            updateButtonStates();
        }
    });
    
    // Select all checkbox
    $('#selectAll').change(function() {
        const isChecked = $(this).prop('checked');
        $('.entry-checkbox').prop('checked', isChecked);
        
        selectedEntries = [];
        if (isChecked && currentDictionary) {
            // Select all entries
            for (let i = 0; i < currentDictionary.dictionaryEntries.length; i++) {
                selectedEntries.push(i);
            }
        }
        
        updateButtonStates();
    });
    
    // Individual checkbox selection
    $('#dictionaryTable').on('change', '.entry-checkbox', function() {
        const index = parseInt($(this).data('index'));
        
        if ($(this).prop('checked')) {
            if (!selectedEntries.includes(index)) {
                selectedEntries.push(index);
            }
        } else {
            const position = selectedEntries.indexOf(index);
            if (position !== -1) {
                selectedEntries.splice(position, 1);
            }
            
            // Uncheck "select all" if any item is unchecked
            $('#selectAll').prop('checked', false);
        }
        
        updateButtonStates();
    });
    
    // Add entry button
    $('#addEntryBtn').click(function() {
        if (!currentDictionary) {
            alert('Please select a dictionary first.');
            return;
        }
        
        // Reset form for new entry
        $('#entryForm')[0].reset();
        $('#entryIndex').val('');
        $('#entryModalLabel').text('Add Entry');
        $('#entryModal').modal('show');
    });
    
    // Edit entry button
    $('#dictionaryTable').on('click', '.edit-btn', function() {
        const index = parseInt($(this).data('index'));
        const entry = currentDictionary.dictionaryEntries[index];
        
        // Populate form with entry data
        $('#entryIndex').val(index);
        $('#fromInput').val(entry.from);
        $('#toInput').val(entry.to);
        $('#knownCheck').prop('checked', entry.known);
        $('#freqInput').val(entry.freq);
        
        $('#entryModalLabel').text('Edit Entry');
        $('#entryModal').modal('show');
    });
    
    // Delete single entry button
    $('#dictionaryTable').on('click', '.delete-btn', function() {
        const index = parseInt($(this).data('index'));
        selectedEntries = [index];
        $('#deleteConfirmModal').modal('show');
    });
    
    // Delete selected entries button
    $('#deleteSelectedBtn').click(function() {
        if (selectedEntries.length > 0) {
            $('#deleteConfirmModal').modal('show');
        }
    });
    
    // Confirm delete button
    $('#confirmDeleteBtn').click(function() {
        if (selectedEntries.length > 0 && currentDictionary) {
            deleteEntries(selectedEntries);
            $('#deleteConfirmModal').modal('hide');
        }
    });
    
    // Save entry button
    $('#saveEntryBtn').click(function() {
        const entryIndex = $('#entryIndex').val();
        const entry = {
            from: $('#fromInput').val(),
            to: $('#toInput').val(),
            known: $('#knownCheck').prop('checked'),
            freq: parseInt($('#freqInput').val())
        };
        
        if (!entry.from || !entry.to) {
            alert('Please fill in all required fields.');
            return;
        }
        
        if (entryIndex === '') {
            // Add new entry
            addEntry(entry);
        } else {
            // Update existing entry
            updateEntry(parseInt(entryIndex), entry);
        }
        
        $('#entryModal').modal('hide');
    });
}

/**
 * Load dictionary data from the server
 * @param {string} fileName - The name of the dictionary file to load
 */
function loadDictionary(fileName) {
    $.ajax({
        url: '/api/dictionaries/' + fileName,
        type: 'GET',
        success: function(data) {
            currentDictionary = data;
            refreshTable();
            selectedEntries = [];
            updateButtonStates();
        },
        error: function(xhr) {
            alert('Error loading dictionary: ' + xhr.responseText);
        }
    });
}

/**
 * Refresh the DataTable with current dictionary data
 */
function refreshTable() {
    if (currentDictionary && currentDictionary.dictionaryEntries) {
        dictionaryTable.clear();
        dictionaryTable.rows.add(currentDictionary.dictionaryEntries).draw();
    } else {
        dictionaryTable.clear().draw();
    }
    
    // Reset selection
    $('#selectAll').prop('checked', false);
    selectedEntries = [];
    updateButtonStates();
}

/**
 * Add a new entry to the current dictionary
 * @param {Object} entry - The entry to add
 */
function addEntry(entry) {
    if (!currentDictionary) return;
    
    const fileName = $('#dictionarySelect').val();
    
    $.ajax({
        url: '/api/dictionaries/' + fileName + '/entries',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(entry),
        success: function() {
            // Reload the dictionary to get updated data
            loadDictionary(fileName);
        },
        error: function(xhr) {
            alert('Error adding entry: ' + xhr.responseText);
        }
    });
}

/**
 * Update an existing entry in the current dictionary
 * @param {number} index - The index of the entry to update
 * @param {Object} entry - The updated entry data
 */
function updateEntry(index, entry) {
    if (!currentDictionary) return;
    
    const fileName = $('#dictionarySelect').val();
    
    $.ajax({
        url: '/api/dictionaries/' + fileName + '/entries/' + index,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(entry),
        success: function() {
            // Reload the dictionary to get updated data
            loadDictionary(fileName);
        },
        error: function(xhr) {
            alert('Error updating entry: ' + xhr.responseText);
        }
    });
}

/**
 * Delete entries from the current dictionary
 * @param {Array} indices - The indices of the entries to delete
 */
function deleteEntries(indices) {
    if (!currentDictionary || indices.length === 0) return;
    
    const fileName = $('#dictionarySelect').val();
    
    $.ajax({
        url: '/api/dictionaries/' + fileName + '/entries',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify(indices),
        success: function() {
            // Reload the dictionary to get updated data
            loadDictionary(fileName);
        },
        error: function(xhr) {
            alert('Error deleting entries: ' + xhr.responseText);
        }
    });
}

/**
 * Update button states based on selection
 */
function updateButtonStates() {
    $('#deleteSelectedBtn').prop('disabled', selectedEntries.length === 0);
}
