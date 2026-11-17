// Logs de Auditoria — dados do PostgreSQL via API

const ACAO_LABEL = {
  CADASTRO_VEICULO: 'Cadastro de veículo',
  PEDIDO_COMPRA: 'Pedido de compra',
  APROVAR_PEDIDO: 'Aprovar pedido',
  REJEITAR_PEDIDO: 'Rejeitar pedido',
  ATUALIZAR_PERFIL: 'Atualizar perfil',
  ATUALIZAR_STATUS: 'Atualizar status',
  CADASTRO: 'Cadastro de usuário',
  LOGIN: 'Login'
};

let logsCache = [];

function formatDateLog(iso) {
  if (!iso) return '—';
  try {
    const d = new Date(iso);
    return d.toLocaleString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  } catch {
    return String(iso);
  }
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function renderLogsFromData(logs) {
  const tbody = document.getElementById('tbody-logs');
  const empty = document.getElementById('empty-logs');
  if (!tbody) return;

  const filtroEl = document.getElementById('filtro-acao');
  const buscaEl = document.getElementById('busca-log');
  const filtro = filtroEl ? filtroEl.value : '';
  const busca = (buscaEl ? buscaEl.value : '').toLowerCase();

  let filtered = logs || [];
  if (filtro) filtered = filtered.filter(l => l.acao === filtro);
  if (busca) {
    filtered = filtered.filter(l =>
      (l.usuario || '').toLowerCase().includes(busca) ||
      (l.detalhes || '').toLowerCase().includes(busca) ||
      (l.email || '').toLowerCase().includes(busca)
    );
  }

  if (filtered.length === 0) {
    tbody.innerHTML = '';
    if (empty) empty.style.display = 'block';
    return;
  }
  if (empty) empty.style.display = 'none';

  tbody.innerHTML = filtered.map(l => `
    <tr>
      <td style="white-space:nowrap">${formatDateLog(l.data)}</td>
      <td>
        <strong>${escapeHtml(l.usuario || '—')}</strong>
        ${l.email ? `<br><small style="color:var(--muted)">${escapeHtml(l.email)}</small>` : ''}
      </td>
      <td><span class="badge badge-info">${ACAO_LABEL[l.acao] || escapeHtml(l.acao || '')}</span></td>
      <td style="max-width:320px">${escapeHtml(l.detalhes || '—')}</td>
      <td>${escapeHtml(l.origem || 'web')}</td>
    </tr>
  `).join('');
}

function renderLogs() {
  fetch('/logs/listar')
    .then(r => {
      if (!r.ok) throw new Error('Falha ao carregar logs');
      return r.json();
    })
    .then(data => {
      logsCache = Array.isArray(data) ? data : [];
      renderLogsFromData(logsCache);
    })
    .catch(err => {
      console.error(err);
      // fallback localStorage se API falhar
      try {
        if (typeof getStore === 'function' && typeof STORE_KEYS !== 'undefined') {
          logsCache = getStore(STORE_KEYS.logs) || [];
        }
      } catch {}
      renderLogsFromData(logsCache);
      const empty = document.getElementById('empty-logs');
      if (empty && logsCache.length === 0) {
        empty.style.display = 'block';
        empty.textContent = 'Não foi possível carregar logs do banco. Verifique a conexão.';
      }
    });
}

function exportarLogs() {
  const logs = logsCache.length ? logsCache : [];
  const blob = new Blob([JSON.stringify(logs, null, 2)], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `automarket-logs-${new Date().toISOString().slice(0, 10)}.json`;
  a.click();
  URL.revokeObjectURL(url);
}

function limparLogs() {
  alert('Os logs de auditoria ficam no banco de dados e não podem ser apagados pelo frontend.');
}

document.addEventListener('DOMContentLoaded', () => {
  renderLogs();
  const filtro = document.getElementById('filtro-acao');
  const busca = document.getElementById('busca-log');
  if (filtro) filtro.addEventListener('change', () => renderLogsFromData(logsCache));
  if (busca) busca.addEventListener('input', () => renderLogsFromData(logsCache));
});
