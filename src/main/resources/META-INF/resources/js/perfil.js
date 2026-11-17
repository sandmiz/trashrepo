// Configuração de Perfil — lê e grava no PostgreSQL (/perfil/buscar e /perfil/salvar)

function preencherPerfil(p) {
  document.getElementById('nome').value = p.nome || '';
  document.getElementById('email').value = p.email || '';
  document.getElementById('telefone').value = p.telefone || '';
  document.getElementById('cpf').value = p.cpf || '';
  document.getElementById('cidade').value = p.cidade || '';
  document.getElementById('estado').value = p.estado || '';
  document.getElementById('tipo').value = p.tipo || 'cliente';
  document.getElementById('bio').value = p.bio || '';
  document.getElementById('senha-atual').value = '';
  document.getElementById('senha-nova').value = '';
}

// Mantém sessão e perfil local iguais ao que está no banco
function sincronizarSessao(dados) {
  const atual = getUsuarioLogado() || {};
  const nova = { ...atual, ...dados };
  localStorage.setItem('automarket_session', JSON.stringify(nova));
  setPerfil(nova);
}

function carregarPerfil() {
  preencherPerfil(getPerfil());
  atualizarSidebar();

  const user = getUsuarioLogado();
  if (!user || user.id == null) return;

  fetch(`/perfil/buscar?id=${encodeURIComponent(user.id)}`)
    .then(r => (r.ok ? r.json() : null))
    .then(p => {
      if (!p) return;
      preencherPerfil(p);
      sincronizarSessao(p);
      atualizarSidebar();
    })
    .catch(console.error);
}

function salvarPerfil(e) {
  e.preventDefault();

  const user = exigirLogin();
  if (!user) return false;

  const perfil = {
    nome: document.getElementById('nome').value.trim(),
    email: document.getElementById('email').value.trim(),
    telefone: document.getElementById('telefone').value.trim(),
    cpf: document.getElementById('cpf').value.trim(),
    cidade: document.getElementById('cidade').value.trim(),
    estado: document.getElementById('estado').value,
    tipo: document.getElementById('tipo').value,
    bio: document.getElementById('bio').value.trim(),
    senhaAtual: document.getElementById('senha-atual').value,
    senhaNova: document.getElementById('senha-nova').value
  };

  if (!perfil.nome || !perfil.email) {
    showAlert('Nome e e-mail são obrigatórios.', 'error');
    return false;
  }
  if (perfil.senhaNova && perfil.senhaNova.length < 6) {
    showAlert('A nova senha deve ter pelo menos 6 caracteres.', 'error');
    return false;
  }

  fetch(`/perfil/salvar?usuarioId=${encodeURIComponent(user.id)}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(perfil)
  })
    .then(async (r) => {
      const data = await r.json().catch(() => ({}));
      if (!r.ok) {
        showAlert(extrairErro(data, 'Erro ao salvar o perfil.'), 'error');
        return;
      }
      sincronizarSessao(data);
      preencherPerfil(data);
      registrarLog('ATUALIZAR_PERFIL', `Perfil atualizado: ${data.nome} (${data.email}) — tipo: ${data.tipo}`);
      showAlert('Perfil atualizado com sucesso!');
      atualizarSidebar();
    })
    .catch(err => {
      console.error(err);
      showAlert('Erro de conexão com o servidor.', 'error');
    });
  return false;
}

document.addEventListener('DOMContentLoaded', carregarPerfil);
