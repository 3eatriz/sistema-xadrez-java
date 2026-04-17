# ♟️ Chess System Java

Um sistema de xadrez desenvolvido em Java utilizando Programação Orientada a Objetos (POO), como parte do curso **"Java Completo Programação Orientada a Objetos + Projetos"**.

O projeto simula uma partida de xadrez completa no terminal, permitindo dois jogadores jogarem entre si (1 vs 1), com regras básicas implementadas e validação de jogadas.

---

## 🚀 Funcionalidades

- Tabuleiro de xadrez no console
- Jogadas alternadas entre dois jogadores
- Validação de movimentos das peças
- Captura de peças
- Indicação de peças possíveis de movimentação
- Controle de turno
- Sistema de posições do tabuleiro (linha/coluna)
- Tratamento de erros de jogadas inválidas

---

## 🧠 Conceitos aplicados

Este projeto foi desenvolvido com foco em:

- Programação Orientada a Objetos (POO)
  - Encapsulamento
  - Herança
  - Polimorfismo
- Tratamento de exceções
- Estruturas de dados
- Lógica de programação avançada
- Modelagem de domínio (tabuleiro, peças, posição, jogo)

---

## 🏗️ Estrutura do projeto

O sistema é dividido em módulos principais:

- `board` → Representação do tabuleiro
- `pieces` → Implementação das peças do jogo
- `chess` → Regras específicas do xadrez
- `application` → Execução do jogo (console)

---

## 🎮 Como executar o projeto

### Pré-requisitos
- Java 11 ou superior
- IDE (IntelliJ, Eclipse ou VS Code)

### Passos

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/chess-system-java.git

# Acesse a pasta do projeto
cd chess-system-java

# Compile o projeto
javac application/Program.java

# Execute o programa
java application.Program
