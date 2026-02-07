const notifyEl = document.getElementById('notify');

function notify(msg, isError) {
  if (!notifyEl) return;
  notifyEl.textContent = msg;
  notifyEl.className = isError ? 'notify error' : 'notify';
  notifyEl.style.opacity = '1';
  setTimeout(()=> notifyEl.style.opacity = '0', 3000);
}

async function apiFetch(path, opts) {
  const res = await fetch(path, Object.assign({headers:{'Content-Type':'application/json'}}, opts));
  const text = await res.text();
  let json = null;
  try { json = text ? JSON.parse(text) : null; } catch (e) { json = null; }
  if (!res.ok) {
    const msg = (json && json.error) ? json.error : (text || res.statusText);
    throw new Error(msg);
  }
  return json;
}

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

function escapeHtml(s){ if (!s) return ''; return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }

document.getElementById('addBook').addEventListener('click', async ()=>{
  const title = document.getElementById('bookTitle').value.trim();
  const author = document.getElementById('bookAuthor').value.trim();
  const isbn = document.getElementById('bookIsbn').value.trim();
  if (!title || !author || !isbn) { notify('Please provide title, author and ISBN', true); return; }
  try {
    await apiFetch('/api/books', {method:'POST', body: JSON.stringify({title,author,isbn})});
    notify('Book added');
    document.getElementById('bookTitle').value=''; document.getElementById('bookAuthor').value=''; document.getElementById('bookIsbn').value='';
    load();
  } catch (err) { notify('Add book failed: '+err.message, true); }
});

document.getElementById('addMember').addEventListener('click', async ()=>{
  const name = document.getElementById('memberName').value.trim();
  const memberId = document.getElementById('memberId').value.trim();
  const email = document.getElementById('memberEmail').value.trim();
  if (!name || !memberId || !email) { notify('Please provide name, id and email', true); return; }
  try {
    await apiFetch('/api/members', {method:'POST', body: JSON.stringify({name,memberId,email})});
    notify('Member added');
    document.getElementById('memberName').value=''; document.getElementById('memberId').value=''; document.getElementById('memberEmail').value='';
    load();
  } catch (err) { notify('Add member failed: '+err.message, true); }
});

document.getElementById('borrowBtn').addEventListener('click', async ()=>{
  const isbn = document.getElementById('actionIsbn').value.trim();
  const memberId = document.getElementById('actionMemberId').value.trim();
  if (!isbn || !memberId) { notify('Provide ISBN and Member ID', true); return; }
  try {
    await apiFetch('/api/borrow', {method:'POST', body: JSON.stringify({isbn, member_id: memberId})});
    notify('Book borrowed');
    load();
  } catch (err) { notify('Borrow failed: '+err.message, true); }
});

document.getElementById('returnBtn').addEventListener('click', async ()=>{
  const isbn = document.getElementById('actionIsbn').value.trim();
  const memberId = document.getElementById('actionMemberId').value.trim();
  if (!isbn || !memberId) { notify('Provide ISBN and Member ID to return', true); return; }
  try {
    await apiFetch('/api/return', {method:'POST', body: JSON.stringify({isbn, member_id: memberId})});
    notify('Book returned');
    load();
  } catch (err) { notify('Return failed: '+err.message, true); }
});

load();
