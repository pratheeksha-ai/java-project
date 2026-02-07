const form = document.getElementById('addBookForm');
form.addEventListener('submit', function(e) {
  e.preventDefault();

  const title = document.getElementById('bookTitle').value.trim();
  const author = document.getElementById('bookAuthor').value.trim();
  const isbn = document.getElementById('bookIsbn').value.trim();

  const books = JSON.parse(localStorage.getItem('books')) || [];

  books.push({
    title,
    author,
    isbn,
    status: 'Available'
  });

  localStorage.setItem('books', JSON.stringify(books));

  // Clear form
  form.reset();

  // Optional: redirect to home or show notification
  alert('Book added successfully!');
});
