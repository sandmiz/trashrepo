// ===========================
// AUTOMARKET — DATA.JS
// Carrega veículos do PostgreSQL via API (com fallback local)
// ===========================

let carrosDB = [];

const PLACEHOLDER_IMG = 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&q=80';

// Escapa texto vindo do banco antes de ir para innerHTML (evita XSS em marca/descrição etc.)
function esc(str) {
  return String(str == null ? '' : str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function corParaHex(nome) {
  const c = coresDisponiveis.find(x => x.nome.toLowerCase() === String(nome || '').toLowerCase());
  return c ? c.hex : '#808080';
}

function mapVeiculoApi(v) {
  return {
    id: v.id,
    marca: esc(v.marca),
    modelo: esc(v.modelo),
    versao: esc(v.versao),
    ano: v.ano,
    km: v.km != null ? v.km : 0,
    preco: v.preco != null ? v.preco : 0,
    cambio: esc(v.cambio || 'manual'),
    combustivel: esc(v.combustivel || 'flex'),
    cor: esc(v.cor),
    corHex: corParaHex(v.cor),
    potencia: v.potencia,
    cidade: esc(v.cidade),
    estado: esc(v.estado),
    descricao: esc(v.descricao),
    telefone: esc(v.telefone),
    imagem: v.imagem || PLACEHOLDER_IMG,
    badge: v.badge || (v.ano >= 2023 ? 'novo' : 'destaque'),
    opcionais: v.opcionais || []
  };
}

function carregarVeiculosDoBanco() {
  return fetch('/api/veiculos')
    .then(r => {
      if (!r.ok) throw new Error('API veiculos falhou');
      return r.json();
    })
    .then(lista => {
      if (Array.isArray(lista) && lista.length > 0) {
        carrosDB = lista.map(mapVeiculoApi);
      }
      return carrosDB;
    })
    .catch(err => {
      console.warn('Falha ao carregar veículos do banco:', err);
      return carrosDB;
    });
}

// Usuários: sessão vem do login no banco
const getUsuarioLogado = () => {
  try {
    const data = localStorage.getItem('automarket_session');
    return data ? JSON.parse(data) : null;
  } catch {
    return null;
  }
};

// Favoritos: guardados no navegador, uma lista por usuário logado
async function getFavoritos() {
  const user = await getPerfil();
  if (!user || user.id == null) return [];
  try {
    return JSON.parse(localStorage.getItem(`am_favoritos_${user.id}`) || '[]');
  } catch {
    return [];
  }
}

async function salvarFavoritos(lista) {
  const user = await getPerfil();
  if (!user || user.id == null) return;
  localStorage.setItem(`am_favoritos_${user.id}`, JSON.stringify(lista));
}

const formatPreco = (v) => `R$ ${Number(v || 0).toLocaleString('pt-BR')}`;
const formatKm = (v) => `${Number(v || 0).toLocaleString('pt-BR')} km`;

const coresDisponiveis = [
  { nome: "Preto", hex: "#1A1A1A" },
  { nome: "Branco", hex: "#F5F5F5" },
  { nome: "Prata", hex: "#C0C0C0" },
  { nome: "Cinza", hex: "#808080" },
  { nome: "Vermelho", hex: "#CC0000" },
  { nome: "Azul", hex: "#003087" },
  { nome: "Verde", hex: "#2D5016" },
  { nome: "Laranja", hex: "#FF6B1A" }
];

// carrega ao iniciar
carregarVeiculosDoBanco().then(() => {
  if (typeof renderFeaturedCars === 'function') renderFeaturedCars();
  if (typeof aplicarFiltros === 'function') aplicarFiltros();
  document.dispatchEvent(new CustomEvent('veiculosCarregados', { detail: carrosDB }));
});
