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

function escapeHtml(s){ if (!s) return ''; return s.replace(/&/g,'&amp;').replace(/</g,'<').replace(/>/g,'>'); }
