function returnBook() {
    const isbn = document.getElementById('return-isbn').value;
    const memberId = document.getElementById('return-member-id').value;
    if (!isbn || !memberId) {
        alert('Please enter ISBN and Member ID');
        return;
    }
    fetch('/api/return', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isbn, memberId })
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            alert('Error: ' + data.error);
        } else {
            alert('Book returned successfully');
            document.getElementById('return-isbn').value = '';
            document.getElementById('return-member-id').value = '';
            loadBooks();
        }
    })
    .catch(error => console.error('Error:', error));
}
