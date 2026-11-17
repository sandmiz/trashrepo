// Cadastro de Veículo — grava no PostgreSQL

function limparForm() {
  const form = document.getElementById('form-veiculo');
  if (form) form.reset();
}

function salvarVeiculo(e) {
  e.preventDefault();

  const user = exigirLogin();
  if (!user) return false;
  const usuarioId = user.id;

  const veiculo = {
    marca: document.getElementById('marca')?.value?.trim(),
    modelo: document.getElementById('modelo')?.value?.trim(),
    versao: document.getElementById('versao')?.value?.trim(),
    ano: Number(document.getElementById('ano')?.value),
    km: Number(document.getElementById('km')?.value) || 0,
    preco: Number(document.getElementById('preco')?.value),
    cambio: document.getElementById('cambio')?.value,
    combustivel: document.getElementById('combustivel')?.value,
    cor: document.getElementById('cor')?.value?.trim(),
    potencia: document.getElementById('potencia')?.value ? Number(document.getElementById('potencia').value) : null,
    cidade: document.getElementById('cidade')?.value?.trim(),
    estado: document.getElementById('estado')?.value?.trim(),
    descricao: document.getElementById('descricao')?.value?.trim(),
    telefone: document.getElementById('telefone')?.value?.trim()
  };

  if (!veiculo.marca || !veiculo.modelo || !veiculo.ano || !veiculo.preco) {
    alert('Preencha marca, modelo, ano e preço.');
    return false;
  }

  const url = `/cadastro-veiculo/salvar?usuarioId=${encodeURIComponent(usuarioId)}`;

  fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(veiculo)
  })
    .then(async (r) => {
      const data = await r.json().catch(() => ({}));
      if (!r.ok) {
        alert(extrairErro(data, 'Erro ao salvar veículo no banco.'));
        return;
      }
      alert('Veículo cadastrado com sucesso no banco de dados!');
      limparForm();
      if (typeof carregarVeiculosDoBanco === 'function') {
        carregarVeiculosDoBanco();
      }
    })
    .catch(err => {
      console.error(err);
      alert('Erro de conexão com o servidor.');
    });
  return false;
}
