// ===========================
// AUTOMARKET — AUTH.JS (API + PostgreSQL)
// ===========================

if (window.location.hash === '#cadastro') showTab('cadastro');

function showTab(tab) {
  document.querySelectorAll('.ftab').forEach(b => b.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(c => c.classList.add('hidden'));
  const content = document.getElementById(`tab-${tab}`);
  if (content) content.classList.remove('hidden');
  const btn = document.querySelector(`[onclick="showTab('${tab}')"]`);
  if (btn) btn.classList.add('active');
  const lgErr = document.getElementById('lg-error-box');
  const cdErr = document.getElementById('cd-error-box');
  if (lgErr) lgErr.style.display = 'none';
  if (cdErr) cdErr.style.display = 'none';
}

function togglePw(inputId, btn) {
  const input = document.getElementById(inputId);
  if (!input) return;
  if (input.type === 'password') {
    input.type = 'text';
    btn.textContent = '🙈';
  } else {
    input.type = 'password';
    btn.textContent = '👁';
  }
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

function checkSenha() {
  const senha = document.getElementById('cd-senha')?.value || '';
  const bar = document.getElementById('pw-strength');
  if (!bar) return;
  let strength = 0;
  if (senha.length >= 6) strength++;
  if (/[A-Z]/.test(senha)) strength++;
  if (/[0-9]/.test(senha)) strength++;
  if (/[^A-Za-z0-9]/.test(senha)) strength++;
  const colors = ['', '#EF4444', '#F97316', '#EAB308', '#22C55E'];
  const widths = ['0%', '25%', '50%', '75%', '100%'];
  bar.style.background = colors[strength];
  bar.style.width = widths[strength];
  bar.style.height = strength > 0 ? '4px' : '0';
}

// getUsuarioLogado() vem de data.js (declarar de novo aqui dava SyntaxError e quebrava o login)

function fazerLogin() {
  const email = document.getElementById('lg-email')?.value?.trim() || '';
  const senha = document.getElementById('lg-senha')?.value || '';
  const lembrar = document.getElementById('lg-lembrar')?.checked;
  const errorBox = document.getElementById('lg-error-box');
  const errorMsg = document.getElementById('lg-error');

  if (!email || !senha) {
    if (errorMsg) errorMsg.textContent = 'Preencha e-mail e senha.';
    if (errorBox) errorBox.style.display = 'block';
    shakeCard();
    return;
  }

  fetch('/login/entrar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha })
  })
    .then(async (response) => {
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        const msg = (typeof data === 'string' ? data : (data.erro || data.message)) || 'E-mail ou senha inválidos.';
        if (errorMsg) errorMsg.textContent = msg;
        if (errorBox) errorBox.style.display = 'block';
        shakeCard();
        return;
      }
      mostrarSucesso(`Bem-vindo, ${data.nome}!`, 'Login realizado com sucesso. Redirecionando...');
    })
    .catch((err) => {
      console.error(err);
      if (errorMsg) errorMsg.textContent = 'Erro de conexão com o servidor.';
      if (errorBox) errorBox.style.display = 'block';
      shakeCard();
    });
}

function fazerCadastro() {
  const nome = document.getElementById('cd-nome')?.value?.trim() || '';
  const sobrenome = document.getElementById('cd-sobrenome')?.value?.trim() || '';
  const email = document.getElementById('cd-email')?.value?.trim() || '';
  const senha = document.getElementById('cd-senha')?.value || '';
  const telefone = document.getElementById('cd-tel')?.value?.trim() || '';
  const errorBox = document.getElementById('cd-error-box');
  const errorMsg = document.getElementById('cd-error');

  if (!nome || !sobrenome || !email || !senha) {
    if (errorMsg) errorMsg.textContent = 'Preencha todos os campos obrigatórios.';
    if (errorBox) errorBox.style.display = 'block';
    return;
  }
  if (!document.getElementById('cd-termos')?.checked) {
    if (errorMsg) errorMsg.textContent = 'Aceite os Termos de Uso para criar a conta.';
    if (errorBox) errorBox.style.display = 'block';
    return;
  }

  fetch('/login/cadastrar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, sobrenome, email, senha, telefone })
  })
    .then(async (response) => {
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        const msg = (typeof data === 'string' ? data : (data.erro || data.message)) || 'Erro ao cadastrar.';
        if (errorMsg) errorMsg.textContent = msg;
        if (errorBox) errorBox.style.display = 'block';
        return;
      }
      mostrarSucesso(`Conta criada, ${nome}!`, 'Cadastro realizado no banco de dados. Bem-vindo!');
    })
    .catch((err) => {
      console.error(err);
      if (errorMsg) errorMsg.textContent = 'Erro de conexão com o servidor.';
      if (errorBox) errorBox.style.display = 'block';
    });
}

function loginSocial(provider) {
  const errorBox = document.getElementById('lg-error-box');
  const errorMsg = document.getElementById('lg-error');
  if (errorMsg) errorMsg.textContent = 'Login social não disponível. Use e-mail e senha.';
  if (errorBox) errorBox.style.display = 'block';
}

function mostrarSucesso(title, sub) {
  const card = document.getElementById('login-card');
  if (card) card.classList.add('hidden');
  const success = document.getElementById('success-card');
  if (success) {
    const msg = document.getElementById('success-msg');
    const subEl = document.getElementById('success-sub');
    if (msg) msg.textContent = title;
    if (subEl) subEl.textContent = sub;
    success.classList.remove('hidden');
  }
  setTimeout(() => {
    window.location.href = '/';
  }, 1800);
}

function shakeCard() {
  const card = document.getElementById('login-card');
  if (!card) return;
  card.style.animation = 'none';
  card.offsetHeight;
  card.style.animation = 'shake .4s ease';
}

const style = document.createElement('style');
style.textContent = `@keyframes shake { 0%,100%{transform:translateX(0)} 20%,60%{transform:translateX(-8px)} 40%,80%{transform:translateX(8px)} }`;
document.head.appendChild(style);

const sessionAtual = getUsuarioLogado();
if (sessionAtual) {
  const redirectMsg = document.createElement('div');
  redirectMsg.style.cssText = 'position:fixed;top:20px;right:20px;background:#16A34A;color:white;padding:12px 20px;border-radius:12px;font-weight:600;z-index:9999;box-shadow:0 4px 20px rgba(0,0,0,.2)';
  redirectMsg.textContent = `✓ Você já está logado como ${sessionAtual.nome}`;
  document.body.appendChild(redirectMsg);
  setTimeout(() => redirectMsg.remove(), 3000);
}
