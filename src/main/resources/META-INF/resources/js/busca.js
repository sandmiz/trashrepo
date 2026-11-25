// ===========================
// AUTOMARKET — BUSCA.JS
// ===========================

let carrosFiltrados = [];
let viewMode = 'grid';
let paginaAtual = 1;
const ITENS_POR_PAGINA = 9;
let corSelecionada = '';

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  inicializarColorOptions();
  lerURLParams();
  atualizarNavLogin();

  function iniciarBusca() {
    carrosFiltrados = [...carrosDB];
    aplicarFiltros();
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (id) {
      const carro = carrosDB.find(c => c.id === parseInt(id));
      if (carro) setTimeout(() => abrirModal(carro), 300);
    }
  }

  if (carrosDB && carrosDB.length > 0) {
    iniciarBusca();
  } else {
    carregarVeiculosDoBanco().then(iniciarBusca);
  }
});

// Navbar scroll
window.addEventListener('scroll', () => {
  document.getElementById('navbar')?.classList.toggle('scrolled', window.scrollY > 40);
});

// Hamburger
document.getElementById('hamburger')?.addEventListener('click', () => {
  document.querySelector('.nav-links')?.classList.toggle('open');
  document.querySelector('.nav-actions')?.classList.toggle('open');
});

async function atualizarNavLogin() {
  const user = await getPerfil();
  const btn = document.getElementById('nav-login-btn');
  if (user && btn) {
    btn.textContent = `👤 ${user.nome}`;
    btn.href = '#';
  }
}

function inicializarColorOptions() {
  const container = document.getElementById('color-options');
  if (!container) return;
  container.innerHTML = coresDisponiveis.map(c => `
    <div class="color-dot" style="background:${c.hex};border:2px solid ${c.hex === '#F5F5F5' ? '#ccc' : c.hex};"
      title="${c.nome}" onclick="selecionarCor('${c.nome}', this)" data-cor="${c.nome}">
    </div>
  `).join('');
}

function selecionarCor(cor, el) {
  if (corSelecionada === cor) {
    corSelecionada = '';
    document.querySelectorAll('.color-dot').forEach(d => d.classList.remove('selected'));
  } else {
    corSelecionada = cor;
    document.querySelectorAll('.color-dot').forEach(d => d.classList.remove('selected'));
    el.classList.add('selected');
  }
  aplicarFiltros();
}

function lerURLParams() {
  const params = new URLSearchParams(window.location.search);
  if (params.get('marca')) document.getElementById('f-marca').value = params.get('marca');
  if (params.get('modelo')) document.getElementById('f-modelo').value = params.get('modelo');
  if (params.get('preco')) document.getElementById('f-preco-max').value = params.get('preco');
  if (params.get('q')) document.getElementById('busca-texto').value = params.get('q');
}

function aplicarFiltros() {
  const marca = document.getElementById('f-marca')?.value || '';
  const modelo = document.getElementById('f-modelo')?.value.toLowerCase() || '';
  const anoMin = parseInt(document.getElementById('f-ano-min')?.value) || 0;
  const anoMax = parseInt(document.getElementById('f-ano-max')?.value) || 9999;
  const precoMin = parseFloat(document.getElementById('f-preco-min')?.value) || 0;
  const precoMax = parseFloat(document.getElementById('f-preco-max')?.value) || Infinity;
  const kmMax = parseInt(document.getElementById('f-km')?.value) || Infinity;
  const busca = document.getElementById('busca-texto')?.value.toLowerCase() || '';

  // Câmbios
  const cambios = [...document.querySelectorAll('.check-options input[type=checkbox][value]')]
    .filter(cb => ['manual','automatico','cvt'].includes(cb.value) && cb.checked)
    .map(cb => cb.value);

  // Combustíveis
  const combustiveis = [...document.querySelectorAll('.check-options input[type=checkbox][value]')]
    .filter(cb => ['flex','gasolina','diesel','eletrico'].includes(cb.value) && cb.checked)
    .map(cb => cb.value);

  const ordenar = document.getElementById('ordenar')?.value || 'recente';

  carrosFiltrados = carrosDB.filter(c => {
    if (marca && c.marca !== marca) return false;
    if (modelo && !c.modelo.toLowerCase().includes(modelo) && !c.versao.toLowerCase().includes(modelo)) return false;
    if (c.ano < anoMin || c.ano > anoMax) return false;
    if (c.preco < precoMin || c.preco > precoMax) return false;
    if (c.km > kmMax) return false;
    if (cambios.length && !cambios.includes(c.cambio)) return false;
    if (combustiveis.length && !combustiveis.includes(c.combustivel)) return false;
    if (corSelecionada && c.cor !== corSelecionada) return false;
    if (busca) {
      const texto = `${c.marca} ${c.modelo} ${c.versao} ${c.cidade} ${c.estado}`.toLowerCase();
      if (!texto.includes(busca)) return false;
    }
    return true;
  });

  // Ordenar
  carrosFiltrados.sort((a, b) => {
    if (ordenar === 'menor-preco') return a.preco - b.preco;
    if (ordenar === 'maior-preco') return b.preco - a.preco;
    if (ordenar === 'menor-km') return a.km - b.km;
    if (ordenar === 'menor-ano') return a.ano - b.ano;
    return b.id - a.id; // mais recentes
  });

  paginaAtual = 1;
  renderizar();
}

async function renderizar() {
  const grid = document.getElementById('resultados-grid');
  const empty = document.getElementById('empty-state');
  const count = document.getElementById('result-count');

  const total = carrosFiltrados.length;
  const inicio = (paginaAtual - 1) * ITENS_POR_PAGINA;
  const fim = inicio + ITENS_POR_PAGINA;
  const pagina = carrosFiltrados.slice(inicio, fim);

  count.textContent = `${total} ${total === 1 ? 'veículo encontrado' : 'veículos encontrados'}`;

  if (total === 0) {
    grid.innerHTML = '';
    empty.classList.remove('hidden');
    document.getElementById('pagination').innerHTML = '';
    return;
  }
  empty.classList.add('hidden');

  grid.className = `cars-grid ${viewMode === 'list' ? 'list-view' : ''}`;
  grid.innerHTML = (await Promise.all(pagina.map(async car => await criarCardHTML(car)))).join('');

  renderPaginacao(total);

  // Animação de entrada
  grid.querySelectorAll('.car-card').forEach((card, i) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(16px)';
    setTimeout(() => {
      card.style.transition = 'opacity .35s ease, transform .35s ease';
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, i * 60);
  });
}

async function criarCardHTML(car) {
  const fav = await isFavorito(car.id);
  return `
    <div class="car-card" onclick="abrirModal(carrosDB.find(c=>c.id===${car.id}))">
      <div class="car-img">
        <img class="car-img-photo" src="${car.imagem}" alt="${car.marca} ${car.modelo}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&q=80'">
        ${car.badge ? `<span class="car-badge ${car.badge}">${car.badge === 'destaque' ? '⭐ Destaque' : '✨ Semi-novo'}</span>` : ''}
        <button class="fav-btn ${fav ? 'active' : ''}" onclick="toggleFavorito(event, ${car.id})">
          ${fav ? '❤️' : '🤍'}
        </button>
      </div>
      <div class="car-info">
        <div class="car-title">${car.marca} ${car.modelo}</div>
        <div class="car-meta">${car.versao} • ${car.ano}</div>
        <div class="car-tags">
          <span class="car-tag">${formatKm(car.km)}</span>
          <span class="car-tag">${car.cambio.charAt(0).toUpperCase() + car.cambio.slice(1)}</span>
          <span class="car-tag">${car.combustivel.charAt(0).toUpperCase() + car.combustivel.slice(1)}</span>
          <span class="car-tag" style="background:${car.corHex};color:${['#F5F5F5','#FFFFFF','#C0C0C0'].includes(car.corHex)?'#333':'white'};border-color:${car.corHex}">${car.cor}</span>
        </div>
        <div class="car-footer">
          <span class="car-price">${formatPreco(car.preco)}</span>
          <span class="car-location">📍 ${car.cidade} - ${car.estado}</span>
        </div>
      </div>
    </div>
  `;
}

function renderPaginacao(total) {
  const totalPages = Math.ceil(total / ITENS_POR_PAGINA);
  const pag = document.getElementById('pagination');
  if (totalPages <= 1) { pag.innerHTML = ''; return; }

  let html = '';
  if (paginaAtual > 1) html += `<button class="page-btn" onclick="irPagina(${paginaAtual - 1})">←</button>`;
  for (let i = 1; i <= totalPages; i++) {
    html += `<button class="page-btn ${i === paginaAtual ? 'active' : ''}" onclick="irPagina(${i})">${i}</button>`;
  }
  if (paginaAtual < totalPages) html += `<button class="page-btn" onclick="irPagina(${paginaAtual + 1})">→</button>`;
  pag.innerHTML = html;
}

function irPagina(p) {
  paginaAtual = p;
  renderizar();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function setView(mode) {
  viewMode = mode;
  document.getElementById('vgrid')?.classList.toggle('active', mode === 'grid');
  document.getElementById('vlist')?.classList.toggle('active', mode === 'list');
  renderizar();
}

function limparFiltros() {
  document.getElementById('f-marca').value = '';
  document.getElementById('f-modelo').value = '';
  document.getElementById('f-ano-min').value = '';
  document.getElementById('f-ano-max').value = '';
  document.getElementById('f-preco-min').value = '';
  document.getElementById('f-preco-max').value = '';
  document.getElementById('f-km').value = '';
  document.getElementById('busca-texto').value = '';
  document.getElementById('ordenar').value = 'recente';
  corSelecionada = '';
  document.querySelectorAll('.color-dot').forEach(d => d.classList.remove('selected'));
  document.querySelectorAll('.check-options input[type=checkbox]').forEach(cb => cb.checked = false);
  aplicarFiltros();
}

function toggleFiltros() {
  document.getElementById('filtros-panel')?.classList.toggle('open');
}

// MODAL
async function abrirModal(car) {
  if (!car) return;
  const modal = document.getElementById('car-modal');
  const content = document.getElementById('modal-content');

  content.innerHTML = `
    <button class="modal-close" onclick="closeModal()">✕</button>
    <div class="modal-hero"><img class="modal-hero-img" src="${car.imagem}" alt="${car.marca} ${car.modelo}" onerror="this.src='https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&q=80'"></div>
    <div class="modal-body">
      <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:12px;margin-bottom:8px">
        <div>
          <div class="modal-title">${car.marca} ${car.modelo}</div>
          <div style="color:var(--muted);font-size:.9rem">${car.versao}</div>
        </div>
        <button class="fav-btn ${await isFavorito(car.id) ? 'active' : ''}" style="position:static;width:44px;height:44px;" onclick="toggleFavorito(event,${car.id});this.textContent=isFavorito(${car.id})?'❤️':'🤍';this.className='fav-btn '+(isFavorito(${car.id})?'active':'')">
          ${await isFavorito(car.id) ? '❤️' : '🤍'}
        </button>
      </div>
      <div class="modal-price">${formatPreco(car.preco)}</div>
      <div class="modal-specs">
        <div class="spec-item"><div class="spec-label">Ano</div><div class="spec-value">${car.ano}</div></div>
        <div class="spec-item"><div class="spec-label">Quilometragem</div><div class="spec-value">${formatKm(car.km)}</div></div>
        <div class="spec-item"><div class="spec-label">Câmbio</div><div class="spec-value">${car.cambio.charAt(0).toUpperCase()+car.cambio.slice(1)}</div></div>
        <div class="spec-item"><div class="spec-label">Combustível</div><div class="spec-value">${car.combustivel.charAt(0).toUpperCase()+car.combustivel.slice(1)}</div></div>
        <div class="spec-item"><div class="spec-label">Cor</div><div class="spec-value" style="display:flex;align-items:center;gap:6px"><span style="width:14px;height:14px;background:${car.corHex};border-radius:50%;border:1px solid #ddd;display:inline-block"></span>${car.cor}</div></div>
        <div class="spec-item"><div class="spec-label">Localização</div><div class="spec-value">${car.cidade} - ${car.estado}</div></div>
      </div>
      <p class="modal-desc">${car.descricao}</p>
      <div style="margin-bottom:20px">
        <div style="font-weight:700;margin-bottom:10px;font-family:var(--font-head)">Opcionais</div>
        <div style="display:flex;flex-wrap:wrap;gap:8px">
          ${car.opcionais.map(o => `<span class="car-tag">✓ ${o}</span>`).join('')}
        </div>
      </div>
      <div class="modal-actions">
        <button class="btn-whatsapp" onclick="contatoWhatsapp(${car.id})">
          📱 WhatsApp do Vendedor
        </button>
        <button class="btn-contact" onclick="agendarVisita(${car.id})">
          📅 Agendar Visita
        </button>
        <button class="btn-contact" onclick="simularFinanciamento(${car.preco})">
          💰 Simular Financiamento
        </button>
      </div>
    </div>
  `;

  modal.classList.remove('hidden');
  document.body.style.overflow = 'hidden';
}

function closeModal() {
  document.getElementById('car-modal').classList.add('hidden');
  document.body.style.overflow = '';
}

document.getElementById('car-modal')?.addEventListener('click', (e) => {
  if (e.target === document.getElementById('car-modal')) closeModal();
});

function contatoWhatsapp(id) {
  const car = carrosDB.find(c => c.id === id);
  if (!car) return;
  const num = String(car.telefone || '').replace(/\D/g, '');
  if (!num) { alert('Este anúncio não tem telefone de contato.'); return; }
  const carro = `${car.marca} ${car.modelo}`;
  const msg = encodeURIComponent(`Olá! Tenho interesse no ${carro} anunciado no AutoMarket. Pode me dar mais informações?`);
  window.open(`https://wa.me/55${num}?text=${msg}`, '_blank');
}

async function agendarVisita(id) {
  const user = await getPerfil();
  if (!user) {
    if (confirm('Faça login para agendar uma visita. Ir para a página de login?')) {
      window.location.href = 'login';
    }
    return;
  }
  alert(`✅ Visita agendada com sucesso!\nEntraremos em contato em até 24 horas para confirmar o horário.`);
}

function simularFinanciamento(preco) {
  const entrada = preco * 0.2;
  const financiado = preco - entrada;
  const parcela12 = (financiado * 1.018) / 12;
  const parcela24 = (financiado * 1.035) / 24;
  const parcela36 = (financiado * 1.054) / 36;
  const parcela48 = (financiado * 1.076) / 48;

  alert(`💰 Simulação de Financiamento\n\nValor do veículo: ${formatPreco(preco)}\nEntrada (20%): ${formatPreco(entrada)}\nValor financiado: ${formatPreco(financiado)}\n\n📊 Parcelas estimadas:\n12x de ${formatPreco(parcela12)}\n24x de ${formatPreco(parcela24)}\n36x de ${formatPreco(parcela36)}\n48x de ${formatPreco(parcela48)}\n\n*Simulação sujeita à análise de crédito.`);
}

// Favoritos
const isFavorito = async (id) => (await getFavoritos()).includes(id);

const toggleFavorito = async (e, id) => {
  e.stopPropagation();
  const user = await getPerfil();
  if (!user) {
    if (confirm('Faça login para salvar favoritos. Ir para login?')) window.location.href = 'login';
    return;
  }
  const favs = await getFavoritos();
  const idx = favs.indexOf(id);
  if (idx === -1) { favs.push(id); mostrarToast('❤️ Adicionado aos favoritos!'); }
  else { favs.splice(idx, 1); mostrarToast('💔 Removido dos favoritos.'); }
  salvarFavoritos(favs);
  renderizar();
};

function mostrarToast(msg) {
  const toast = document.createElement('div');
  toast.style.cssText = 'position:fixed;bottom:24px;left:50%;transform:translateX(-50%);background:#1A1A1A;color:white;padding:12px 24px;border-radius:99px;font-weight:600;z-index:9999;box-shadow:0 8px 32px rgba(0,0,0,.3);font-family:var(--font-body);font-size:.9rem;animation:fadeUp .3s ease';
  toast.textContent = msg;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 2500);
}
