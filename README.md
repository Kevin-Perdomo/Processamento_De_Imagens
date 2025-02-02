# ImageJ

<p align="center">
  <img src="https://s3-ap-northeast-1.amazonaws.com/xlab-leica-microsystems/wordpress/wp-content/uploads/title.jpg" alt="Bem-vindo ao seu Ambiente de Desenvolvimento">
</p>

# Projeto de Processamento de Sinais com ImageJ

- Este projeto utiliza o ImageJ com plugins customizados em Java para realizar tarefas específicas na disciplina de Processamento de Sinais, de forma que possa ser usado qualquer IDE de preferência. Abaixo está o passo a passo do que foi feito.

## Índice

- [Introdução](#introdução)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Uso](#uso)

## Introdução

#### O ImageJ é uma plataforma poderosa para processamento de imagens. Este projeto visa estender suas funcionalidades através de plugins desenvolvidos em Java, facilitando tarefas específicas da disciplina.

#### O Apache Ant é uma ferramenta de automação de build para projetos de software, especialmente em Java. Ele é usado para compilar código, empacotar aplicativos, executar testes, criar documentação e realizar outras tarefas de construção de software.

#### O Java Development Kit (JDK) é um conjunto de ferramentas para o desenvolvimento de aplicações em Java. Ele inclui:

- Java Compiler (javac): Converte código fonte Java em bytecode executável pela Java Virtual Machine (JVM).

- Java Runtime Environment (JRE): Contém a JVM e bibliotecas necessárias para executar programas Java.

- Ferramentas de Desenvolvimento: Inclui utilitários como javadoc, jar, e jdb.

- Bibliotecas Padrão: Fornece funcionalidades comuns, como manipulação de arquivos e redes.

## Requisitos

Antes de começar, você precisará ter os seguintes itens instalados:

- [ImageJ](https://imagej.net/ij/index.html)
- [Apache Ant](https://ant.apache.org/bindownload.cgi)
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

## Instalação

1. **Clone o repositório**:
```bash
git clone <https://github.com/Kevin-Perdomo/Processamento_De_Imagens.git>
cd <imagej>
```

2. **instale o Apache Ant**:
```bash  
sudo apt update
sudo apt install ant
ant -version
```

3. **instale Java 11 (Serve qualquer versão a partir da 8)**:

Para instalar o ambiente de execução do OpenJDK:
```bash  
sudo apt install openjdk-11-jre
```

Para instalar o ambiente de desenvolvimento do OpenJDK:
```bash  
sudo apt install openjdk-11-jdk
```

Para ver sua versão do Java:
```bash  
java --version
```

4. **Baixe o ImageJ a parte (Opcional)**:
- [ImageJ](https://imagej.net/ij/download.html)

## Uso

1. **Importante**: Os nomes dos plugins precisam, obrigatoriamente, ter um caractere underscore para que o ImageJ assim os reconheça. Exemplo: no ImageJ, o plugin "Open" está associado ao comando "Open_", o que é o suficiente para que o ImageJ reconheça este plugin. 

2. **Compile o projeto**:
   ```bash  
    ant run
   ```
