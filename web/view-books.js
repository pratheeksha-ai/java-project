 async function load() {
  try {
    const books = await apiFetch('/api/books', {method:'GET'});
    const members = await apiFetch('/api/members', {method:'GET'});

    const booksTbl = document.getElementById('books');
    booksTbl.innerHTML = '';
    books.forEach(b => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${escapeHtml(b.title)}</td><td>${escapeHtml(b.author)}</td><td>${escapeHtml(b.isbn)}</td><td>${b.isAvailable ? 'Available' : 'Borrowed'}</td><td><button class="delete-btn" data-isbn="${escapeHtml(b.isbn)}">Delete</button></td>`;
      booksTbl.appendChild(tr);
    });

    // attach delete handlers
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const isbn = e.target.getAttribute('data-isbn');
        if (confirm('Delete book ' + isbn + '?')) {
          try {
            await apiFetch('/api/books/delete', {method:'POST', body: JSON.stringify({isbn})});
            notify('Book deleted');
            load();
          } catch (err) { notify('Delete failed: '+err.message, true); }
        }
      });
    });

    const membersTbl = document.getElementById('members');
    membersTbl.innerHTML = '';
    members.forEach(m => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${escapeHtml(m.name)}</td><td>${escapeHtml(m.memberId)}</td><td>${escapeHtml(m.email)}</td><td><button class="delete-member-btn" data-member-id="${escapeHtml(m.memberId)}">Delete</button></td>`;
      membersTbl.appendChild(tr);
    });

    // attach member delete handlers
    document.querySelectorAll('.delete-member-btn').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const memberId = e.target.getAttribute('data-member-id');
        if (confirm('Delete member ' + memberId + '?')) {
          try {
            await apiFetch('/api/members/delete', {method:'POST', body: JSON.stringify({memberId})});
            notify('Member deleted');
            load();
          } catch (err) { notify('Delete failed: '+err.message, true); }
        }
      });
    });
  } catch (err) {
    notify('Load failed: ' + err.message, true);
  }
}

load();
