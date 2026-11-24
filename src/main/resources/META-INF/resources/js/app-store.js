// ===========================
// AUTOMARKET — APP STORE (localStorage)
// Simula backend para os casos de uso
// ===========================

const STORE_KEYS = {
  veiculos: 'am_veiculos',
  pedidos: 'am_pedidos',
  perfil: 'am_perfil',
  logs: 'am_logs',
  usuario: 'am_usuario'
};

function getStore(key) {
  try {
    return JSON.parse(localStorage.getItem(key) || '[]');
  } catch {
    return [];
  }
}

function setStore(key, data) {
  localStorage.setItem(key, JSON.stringify(data));
}

function getPerfil() {
  try {
    const p = localStorage.getItem(STORE_KEYS.perfil);
    if (p) return JSON.parse(p);
  } catch {}
  return {
    nome: 'Usuário Demo',
    email: 'demo@automarket.com',
    telefone: '(11) 99999-0000',
    cpf: '',
    cidade: 'São Paulo',
    estado: 'SP',
    tipo: 'cliente',
    bio: ''
  };
}

function setPerfil(perfil) {
  localStorage.setItem(STORE_KEYS.perfil, JSON.stringify(perfil));
}

// Sessão gravada no login (id, nome, e-mail, tipo). Antes só data.js definia isso, então as
// páginas que carregam apenas app-store.js (pedido, cadastro de veículo...) enxergavam "sem login".
function getUsuarioLogado() {
  try {
    const s = localStorage.getItem('automarket_session');
    return s ? JSON.parse(s) : null;
  } catch {
    return null;
  }
}

function getUsuarioAtual() {
  return getUsuarioLogado() || getPerfil();
}

function escapeHtml(str) {
  return String(str == null ? '' : str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// O backend devolve erro ora como {erro: "..."}, ora como string JSON pura.
function extrairErro(data, padrao) {
  if (typeof data === 'string' && data) return data;
  if (data && (data.erro || data.message)) return data.erro || data.message;
  return padrao;
}

// Páginas que gravam dados precisam de usuário logado; senão manda para o login.
function exigirLogin() {
  const u = getUsuarioLogado();
  if (u && u.id != null) return u;
  if (confirm('Faça login para continuar. Ir para a página de login?')) {
    window.location.href = '/login';
  }
  return null;
}

function formatMoney(v) {
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
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
  return `<span class="badge ${map[status] || 'badge-info'}">${escapeHtml(status)}</span>`;
}

function maskPhone(input) {
  let v = input.value.replace(/\D/g, '').slice(0, 11);
  if (v.length <= 10) {
    v = v.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
  } else {
    v = v.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3');
  }
  input.value = v;
}

function maskCpf(input) {
  let v = input.value.replace(/\D/g, '').slice(0, 11);
  v = v.replace(/(\d{3})(\d)/, '$1.$2')
       .replace(/(\d{3})(\d)/, '$1.$2')
       .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  input.value = v;
}

function showAlert(msg, type = 'success') {
  const el = document.getElementById('alert');
  if (!el) return;
  el.className = 'alert alert-' + type;
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => { el.style.display = 'none'; }, 4500);
}

function atualizarSidebar() {
  const u = getUsuarioAtual();
  const av = document.getElementById('user-avatar');
  const nm = document.getElementById('user-name');
  const rl = document.getElementById('user-role');
  if (av) av.textContent = (u.nome || 'U').charAt(0).toUpperCase();
  if (nm) nm.textContent = u.nome || 'Usuário';
  if (rl) {
    const labels = { cliente: 'Cliente', vendedor: 'Vendedor', admin: 'Admin' };
    rl.textContent = labels[u.tipo] || u.tipo || 'Usuário';
  }
}

// Dados de veículos, pedidos e logs vêm do PostgreSQL via API.
// localStorage guarda só a sessão do usuário (id, nome, e-mail, tipo).
function ensureSeedVeiculos() {
  return getStore(STORE_KEYS.veiculos) || [];
}

document.addEventListener('DOMContentLoaded', () => {
  atualizarSidebar();
});
