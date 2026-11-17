// Admin pedidos — lista e atualiza status no PostgreSQL
// (helpers escapeHtml/formatMoney/formatDate/badgeStatus vêm de app-store.js)

let pedidosCache = [];
let pedidoParaRejeitar = null;

function labelPagamento(v) {
  const map = {
    avista: 'À vista',
    'a vista': 'À vista',
    financiamento: 'Financiamento',
    consorcio: 'Consórcio',
    permuta: 'Permuta + dinheiro'
  };
  return map[v] || v || '—';
}

function mapPedido(p) {
  const veiculo = p.veiculo || {};
  const usuario = p.usuario || {};
  return {
    id: p.id,
    status: p.status,
    valorProposta: p.valorProposta,
    formaPagamento: p.formaPagamento,
    mensagem: p.mensagem,
    criadoEm: p.criadoEm,
    veiculoLabel: `${veiculo.marca || ''} ${veiculo.modelo || ''} ${veiculo.ano || ''}`.trim() || 'Veículo',
    veiculoPreco: veiculo.preco,
    clienteNome: usuario.nome ? `${usuario.nome} ${usuario.sobrenome || ''}`.trim() : 'Cliente',
    clienteEmail: usuario.email || '—',
    clienteTelefone: usuario.telefone || '—'
  };
}

function atualizarEstatisticas(pedidos) {
  const conta = (st) => pedidos.filter(p => p.status === st).length;
  const set = (id, v) => { const el = document.getElementById(id); if (el) el.textContent = v; };
  set('stat-pendente', conta('pendente'));
  set('stat-aprovado', conta('aprovado'));
  set('stat-rejeitado', conta('rejeitado'));
  set('stat-total', pedidos.length);
}

function desenharPedidosAdmin() {
  const container = document.getElementById('lista-admin');
  if (!container) return;

  const filtro = document.getElementById('filtro-status')?.value || '';
  const busca = (document.getElementById('busca-pedido')?.value || '').trim().toLowerCase();

  let pedidos = pedidosCache;
  if (filtro) pedidos = pedidos.filter(p => p.status === filtro);
  if (busca) {
    pedidos = pedidos.filter(p =>
      `${p.clienteNome} ${p.clienteEmail} ${p.veiculoLabel}`.toLowerCase().includes(busca));
  }

  if (pedidos.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="icon">✅</div>
        <h3>Nenhum pedido neste filtro</h3>
        <p>Quando clientes enviarem pedidos, eles aparecerão aqui (dados do banco).</p>
      </div>`;
    return;
  }

  container.innerHTML = pedidos.map(p => `
    <div class="pedido-card">
      <div class="pedido-card-header">
        <div>
          <h3>${escapeHtml(p.veiculoLabel)}</h3>
          <small style="color:var(--muted)">Pedido #${p.id}</small>
        </div>
        ${badgeStatus(p.status)}
      </div>
      <div class="pedido-meta">
        <span>👤 ${escapeHtml(p.clienteNome)}</span>
        <span>📧 ${escapeHtml(p.clienteEmail)}</span>
        <span>📱 ${escapeHtml(p.clienteTelefone)}</span>
        <span>💰 Proposta: <strong>${formatMoney(p.valorProposta)}</strong></span>
        <span>🏷️ Anúncio: ${formatMoney(p.veiculoPreco)}</span>
        <span>💳 ${escapeHtml(labelPagamento(p.formaPagamento))}</span>
        <span>📅 ${formatDate(p.criadoEm)}</span>
      </div>
      ${p.mensagem ? `<p style="font-size:.9rem;color:var(--gray2);margin-bottom:12px">Mensagem: “${escapeHtml(p.mensagem)}”</p>` : ''}
      ${p.status === 'pendente' ? `
        <div class="pedido-actions">
          <button class="btn-success" onclick="aprovarPedido(${p.id})">✓ Aprovar</button>
          <button class="btn-danger" onclick="rejeitarPedido(${p.id})">✕ Rejeitar</button>
        </div>
      ` : ''}
    </div>
  `).join('');
}

// Busca os pedidos no banco, atualiza os contadores e desenha a lista.
function renderPedidosAdmin() {
  const container = document.getElementById('lista-admin');
  if (!container) return;

  fetch('/admin-pedidos/listar')
    .then(r => {
      if (!r.ok) throw new Error('Falha ao listar pedidos');
      return r.json();
    })
    .then(lista => {
      pedidosCache = (Array.isArray(lista) ? lista : []).map(mapPedido);
      atualizarEstatisticas(pedidosCache);
      desenharPedidosAdmin();
    })
    .catch(err => {
      console.error(err);
      container.innerHTML = `<p style="color:#b91c1c">Erro ao carregar pedidos do banco.</p>`;
    });
}

function atualizarStatusPedido(id, status, motivo) {
  let adminNome = 'Admin';
  let adminEmail = '';
  const s = getUsuarioLogado();
  if (s) {
    adminNome = [s.nome, s.sobrenome].filter(Boolean).join(' ') || 'Admin';
    adminEmail = s.email || '';
  }
  const params = new URLSearchParams({ adminNome, adminEmail });
  if (motivo) params.set('motivo', motivo);

  fetch('/admin-pedidos/atualizar-status?' + params.toString(), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ id, status })
  })
    .then(async (r) => {
      const data = await r.json().catch(() => ({}));
      if (!r.ok) {
        alert(extrairErro(data, 'Erro ao atualizar status.'));
        return;
      }
      alert(`Pedido #${id} atualizado para: ${status}`);
      renderPedidosAdmin();
    })
    .catch(err => {
      console.error(err);
      alert('Erro de conexão com o servidor.');
    });
}

function aprovarPedido(id) {
  atualizarStatusPedido(id, 'aprovado');
}

// Abre o modal de rejeição (o HTML já tinha o modal, mas faltavam as funções)
function rejeitarPedido(id) {
  pedidoParaRejeitar = id;
  const motivo = document.getElementById('motivo-rejeicao');
  if (motivo) motivo.value = '';
  document.getElementById('modal-rejeitar')?.classList.add('open');
}

function fecharModal() {
  pedidoParaRejeitar = null;
  document.getElementById('modal-rejeitar')?.classList.remove('open');
}

function confirmarRejeicao() {
  if (pedidoParaRejeitar == null) return;
  const motivo = (document.getElementById('motivo-rejeicao')?.value || '').trim();
  const id = pedidoParaRejeitar;
  fecharModal();
  atualizarStatusPedido(id, 'rejeitado', motivo);
}

// O HTML já liga os filtros com onchange/oninput="renderPedidosAdmin()"; aqui só filtramos o que já foi carregado
function renderPedidosAdminLocal() {
  desenharPedidosAdmin();
}

document.addEventListener('DOMContentLoaded', renderPedidosAdmin);
