let transactions = [];

const isbnInput = document.getElementById('actionIsbn');
const memberInput = document.getElementById('actionMemberId');

/* Borrow Book */
document.getElementById('borrowBtn').addEventListener('click', async () => {
  const isbn = isbnInput.value.trim();
  const member_id = memberInput.value.trim();

  if (!isbn || !member_id) {
    notify('ISBN and Member ID are required', true);
    return;
  }

  try {
   await apiFetch('/api/borrow', {
  method: 'POST',
  body: JSON.stringify({
    isbn: isbn,
    member_id: member_id
  })
});


    notify('Book borrowed successfully');
    isbnInput.value = '';
    memberInput.value = '';

  } catch (err) {
    notify(err.message || 'Borrow failed', true);
  }
});

/* Return Book */
document.getElementById('returnBtn').addEventListener('click', async () => {
  const isbn = isbnInput.value.trim();
  const member_id = memberInput.value.trim();

  if (!isbn || !member_id) {
    notify('ISBN and Member ID are required for return', true);
    return;
  }

  try {
    await apiFetch('/api/return', {
  method: 'POST',
  body: JSON.stringify({
    isbn: isbn,
    member_id: member_id
  })
});



    notify('Book returned successfully');
    isbnInput.value = '';
    memberInput.value = '';

  } catch (err) {
    notify(err.message || 'Return failed', true);
  }
});


