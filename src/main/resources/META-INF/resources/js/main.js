// ===========================
// AUTOMARKET — MAIN.JS (index)
// ===========================

// Navbar scroll
window.addEventListener('scroll', () => {
  const nb = document.getElementById('navbar');
  if (nb) nb.classList.toggle('scrolled', window.scrollY > 40);
});

// Hamburger menu
document.getElementById('hamburger')?.addEventListener('click', () => {
  document.querySelector('.nav-links')?.classList.toggle('open');
  document.querySelector('.nav-actions')?.classList.toggle('open');
});

// Verificar se usuário está logado
const atualizarNav = () => {
  const user = getUsuarioLogado();
  const loginBtn = document.getElementById('nav-login-btn');
  if (user && loginBtn) {
    loginBtn.textContent = `👤 ${user.nome}`;
    loginBtn.href = '#';
  }
};
atualizarNav();

// Renderizar featured cars
const renderFeaturedCars = () => {
  const grid = document.getElementById('featured-cars');
  if (!grid) return;

  const featured = carrosDB.filter(c => c.badge === 'destaque' || c.badge === 'novo').slice(0, 8);

  grid.innerHTML = featured.map(car => `
    <div class="car-card" onclick="abrirCarro(${car.id})">
      <div class="car-img">
        <img class="car-img-photo" src="${car.imagem}" alt="${car.marca} ${car.modelo}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&q=80'">
        ${car.badge ? `<span class="car-badge ${car.badge}">${car.badge === 'destaque' ? '⭐ Destaque' : car.badge === 'novo' ? '✨ Semi-novo' : car.badge}</span>` : ''}
        <button class="fav-btn ${isFavorito(car.id) ? 'active' : ''}" onclick="toggleFavorito(event, ${car.id})">
          ${isFavorito(car.id) ? '❤️' : '🤍'}
        </button>
      </div>
      <div class="car-info">
        <div class="car-title">${car.marca} ${car.modelo}</div>
        <div class="car-meta">${car.versao} • ${car.ano}</div>
        <div class="car-tags">
          <span class="car-tag">${formatKm(car.km)}</span>
          <span class="car-tag">${car.cambio.charAt(0).toUpperCase() + car.cambio.slice(1)}</span>
          <span class="car-tag">${car.combustivel.charAt(0).toUpperCase() + car.combustivel.slice(1)}</span>
        </div>
        <div class="car-footer">
          <span class="car-price">${formatPreco(car.preco)}</span>
          <span class="car-location">📍 ${car.cidade} - ${car.estado}</span>
        </div>
      </div>
    </div>
  `).join('');
};

const isFavorito = (id) => getFavoritos().includes(id);

const toggleFavorito = (e, id) => {
  e.stopPropagation();
  const user = getUsuarioLogado();
  if (!user) {
    window.location.href = 'login';
    return;
  }
  const favs = getFavoritos();
  const idx = favs.indexOf(id);
  if (idx === -1) favs.push(id);
  else favs.splice(idx, 1);
  salvarFavoritos(favs);
  renderFeaturedCars();
};

const abrirCarro = (id) => {
  window.location.href = `busca?id=${id}`;
};

// Hero search
const buscar = () => {
  const marca = document.getElementById('hero-marca')?.value || '';
  const modelo = document.getElementById('hero-modelo')?.value || '';
  const preco = document.getElementById('hero-preco')?.value || '';
  const params = new URLSearchParams();
  if (marca) params.set('marca', marca);
  if (modelo) params.set('modelo', modelo);
  if (preco) params.set('preco', preco);
  window.location.href = `busca?${params.toString()}`;
};

// Search tabs
document.querySelectorAll('.stab').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.stab').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  });
});

// Scroll reveal
const observer = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) {
      e.target.style.opacity = '1';
      e.target.style.transform = 'translateY(0)';
    }
  });
}, { threshold: 0.1 });

document.querySelectorAll('.car-card, .step-card, .brand-card').forEach(el => {
  el.style.opacity = '0';
  el.style.transform = 'translateY(24px)';
  el.style.transition = 'opacity .5s ease, transform .5s ease';
  observer.observe(el);
});

// Init
renderFeaturedCars();

// Navbar active on scroll
const sections = document.querySelectorAll('section[id]');
window.addEventListener('scroll', () => {
  let current = '';
  sections.forEach(s => {
    if (window.scrollY >= s.offsetTop - 100) current = s.getAttribute('id');
  });
  document.querySelectorAll('.nav-links a').forEach(a => {
    a.classList.remove('active');
    if (a.getAttribute('href') === `#${current}`) a.classList.add('active');
  });
});


document.addEventListener('veiculosCarregados', () => {
  if (typeof renderFeaturedCars === 'function') renderFeaturedCars();
});
