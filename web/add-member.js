document.addEventListener("DOMContentLoaded", () => {
  document
    .getElementById("addMemberForm")
    .addEventListener("submit", addMember);

  loadMembers();
});

async function addMember(e) {
  e.preventDefault();

  const name = document.getElementById("memberName").value.trim();
  const memberId = document.getElementById("memberId").value.trim();
  const email = document.getElementById("memberEmail").value.trim();

  if (!name || !memberId || !email) {
    notify("Please provide name, member ID, and email", true);
    return;
  }

  try {
    await apiFetch("/api/members", { method: "POST", body: JSON.stringify({ name, memberId, email }) });
    notify("Member added");
    document.getElementById("addMemberForm").reset();
    loadMembers();
  } catch (err) {
    notify("Add member failed: " + err.message, true);
  }
}

async function loadMembers() {
  try {
    const members = await apiFetch("/api/members");
    const tbody = document.getElementById("members");
    tbody.innerHTML = "";

    members.forEach(m => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${escapeHtml(m.name)}</td>
        <td>${escapeHtml(m.memberId)}</td>
        <td>${escapeHtml(m.email)}</td>
        <td><button class="delete-btn" data-member-id="${escapeHtml(m.memberId)}">Delete</button></td>
      `;
      tbody.appendChild(row);
    });

    // Attach delete handlers
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const memberId = e.target.getAttribute('data-member-id');
        if (confirm('Delete member ' + memberId + '?')) {
          try {
            await apiFetch('/api/members/delete', { method: 'POST', body: JSON.stringify({ memberId }) });
            notify('Member deleted');
            loadMembers();
          } catch (err) {
            notify('Delete failed: ' + err.message, true);
          }
        }
      });
    });
  } catch (err) {
    notify("Load members failed: " + err.message, true);
  }
}
