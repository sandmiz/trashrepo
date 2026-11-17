// Pedido de compra — grava no PostgreSQL

function formatMoney(v) {
  return Number(v || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

function badgeStatus(status) {
  const map = {
    pendente: 'badge-pendente',
    aprovado: 'badge-aprovado',
    rejeitado: 'badge-rejeitado'
  };
  return `<span class="badge ${map[status] || 'badge-info'}">${status || '—'}</span>`;
}

function carregarSelectVeiculos() {
  const sel = document.getElementById('veiculo-id');
  if (!sel) return;
  fetch('/api/veiculos')
    .then(r => r.json())
    .then(lista => {
      if (!Array.isArray(lista)) return;
      sel.innerHTML = '<option value="">Selecione um veículo</option>' +
        lista.map(v => `<option value="${v.id}" data-preco="${v.preco || 0}">${escapeHtml(v.marca)} ${escapeHtml(v.modelo)} ${escapeHtml(v.ano)} — R$ ${Number(v.preco || 0).toLocaleString('pt-BR')}</option>`).join('');
    })
    .catch(console.error);
}

function atualizarPreco() {
  const sel = document.getElementById('veiculo-id');
  const opt = sel?.selectedOptions?.[0];
  const preco = opt ? Number(opt.dataset.preco || 0) : 0;
  const valor = document.getElementById('valor-proposta');
  if (valor && preco > 0 && !valor.value) valor.value = preco;
}

function enviarPedido(e) {
  if (e) e.preventDefault();

  const user = exigirLogin();
  if (!user) return false;
  const usuarioId = user.id;

  const veiculoId = Number(document.getElementById('veiculo-id')?.value);
  const valorProposta = Number(document.getElementById('valor-proposta')?.value);
  const formaPagamento = document.getElementById('forma-pagamento')?.value;
  const mensagem = document.getElementById('mensagem')?.value || '';

  if (!veiculoId || !valorProposta || !formaPagamento) {
    alert('Preencha veículo, valor da proposta e forma de pagamento.');
    return false;
  }

  const body = { veiculoId, valorProposta, formaPagamento, mensagem };
  const url = `/pedido-compra/enviar?usuarioId=${encodeURIComponent(usuarioId)}`;

  fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
    .then(async (r) => {
      const data = await r.json().catch(() => ({}));
      if (!r.ok) {
        alert(extrairErro(data, 'Erro ao enviar pedido.'));
        return;
      }
      alert('Pedido enviado com sucesso e gravado no banco!');
      document.getElementById('form-pedido')?.reset();
      renderMeusPedidos();
    })
    .catch(err => {
      console.error(err);
      alert('Erro de conexão com o servidor.');
    });
  return false;
}

function renderMeusPedidos() {
  const container = document.getElementById('lista-pedidos');
  if (!container) return;
  const filtro = document.getElementById('filtro-status')?.value || '';

  const user = getUsuarioLogado();
  if (!user || user.id == null) {
    container.innerHTML = '<div class="empty-state"><h3>Faça login</h3><p>Entre na sua conta para ver seus pedidos.</p></div>';
    return;
  }

  fetch(`/pedido-compra/listar?usuarioId=${encodeURIComponent(user.id)}`)
    .then(r => {
      if (!r.ok) throw new Error('Falha ao listar pedidos');
      return r.json();
    })
    .then(lista => {
      let pedidos = Array.isArray(lista) ? lista : [];
      if (filtro) pedidos = pedidos.filter(p => p.status === filtro);

      if (pedidos.length === 0) {
        container.innerHTML = `<div class="empty-state"><h3>Nenhum pedido</h3><p>Seus pedidos do banco aparecerão aqui.</p></div>`;
        return;
      }
      container.innerHTML = pedidos.map(p => {
        const v = p.veiculo || {};
        return `
        <div class="pedido-card">
          <div class="pedido-card-header">
            <div>
              <h3>${escapeHtml(v.marca)} ${escapeHtml(v.modelo)} ${escapeHtml(v.ano)}</h3>
              <small>Pedido #${p.id}</small>
            </div>
            ${badgeStatus(p.status)}
          </div>
          <div class="pedido-meta">
            <span>💰 ${formatMoney(p.valorProposta)}</span>
            <span>💳 ${escapeHtml(p.formaPagamento || '—')}</span>
            <span>📅 ${formatDate(p.criadoEm)}</span>
          </div>
          ${p.mensagem ? `<p>${escapeHtml(p.mensagem)}</p>` : ''}
        </div>`;
      }).join('');
    })
    .catch(err => {
      console.error(err);
      container.innerHTML = '<p>Erro ao carregar pedidos do banco.</p>';
    });
}

document.addEventListener('DOMContentLoaded', () => {
  carregarSelectVeiculos();
  renderMeusPedidos();
});
