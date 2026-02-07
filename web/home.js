async function getLibraryData() {
  try {
    const stats = await apiFetch('/api/stats');
    return stats;
  } catch (error) {
    console.error('Error fetching library data:', error);
    return {
      totalBooks: 0,
      availableBooks: 0,
      totalMembers: 0
    };
  }
}

async function updateDashboard() {
  const data = await getLibraryData();
  document.getElementById('total-books').textContent = data.totalBooks;
  document.getElementById('available-books').textContent = data.availableBooks;
  document.getElementById('total-members').textContent = data.totalMembers;
}

// Call when page loads
window.addEventListener('DOMContentLoaded', updateDashboard);
