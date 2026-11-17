@echo off
chcp 65001 >nul
echo ========================================
echo  AutoMarket - Criar banco PostgreSQL
echo ========================================
echo.
echo Usuario: postgres
echo Banco a criar: automarket
echo.
echo Digite a senha do usuario postgres quando pedir.
echo.

REM Tenta usar psql do PATH
where psql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
  if exist "C:\Program Files\PostgreSQL\14\bin\psql.exe" (
    set "PSQL=C:\Program Files\PostgreSQL\14\bin\psql.exe"
  ) else if exist "C:\Program Files\PostgreSQL\15\bin\psql.exe" (
    set "PSQL=C:\Program Files\PostgreSQL\15\bin\psql.exe"
  ) else if exist "C:\Program Files\PostgreSQL\16\bin\psql.exe" (
    set "PSQL=C:\Program Files\PostgreSQL\16\bin\psql.exe"
  ) else if exist "C:\Program Files\PostgreSQL\17\bin\psql.exe" (
    set "PSQL=C:\Program Files\PostgreSQL\17\bin\psql.exe"
  ) else (
    echo [ERRO] psql nao encontrado.
    echo Instale o PostgreSQL ou adicione a pasta bin ao PATH.
    echo Exemplo: C:\Program Files\PostgreSQL\14\bin
    pause
    exit /b 1
  )
) else (
  set "PSQL=psql"
)

echo Criando banco automarket...
"%PSQL%" -U postgres -d postgres -c "CREATE DATABASE automarket OWNER postgres ENCODING 'UTF8';"
if %ERRORLEVEL% EQU 0 (
  echo.
  echo [OK] Banco automarket criado com sucesso!
) else (
  echo.
  echo Se apareceu "already exists" / "ja existe", esta tudo certo.
  echo O banco ja existe - pode subir o Quarkus.
)

echo.
echo Agora no IntelliJ rode: quarkus:dev
echo Tabelas e dados serao criados automaticamente pelo Hibernate + DadosIniciais.java
echo.
pause
