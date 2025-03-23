# Algoritmo -> Componentes Conexos

### **Descrição:**
**Componentes Conexos** é um algoritmo de rotulação de componentes conectados em uma imagem binária.  
O algoritmo é baseado em uma busca em largura (BFS) e utiliza uma fila FIFO para armazenar os pixels a serem visitados.  
O algoritmo é simples e eficiente, e é capaz de rotular componentes conectados em tempo linear.  

### **Entrada:**  
Imagem Binária **Î = (Di, I)**, e Relação de Adjacência **A**;

### **Saída:**  
Imagem Rotulada **Ĵ = (Di, J)**, onde, inicialmente, **J(p) = 0 ∀ p ∈ Dj**, tal que **p** é um pixel da imagem e **Dj** é o domínio da Imagem **J**.  
Note que **Dj = D1**;

### **Auxiliares:**  
- **FIFO Q**  
- Variável inteira **label = 1**

## **Algoritmo:**  
```bash  
Para todo pixel p ∈ Di, tal que I(p) ≠ 0 e J(p) = 0, faça:

    J(p) = label
    Insira p em Q

    Enquanto Q ≠ ∅ //não está vazia

        Remova p de Q

        Para todo q ∈ A(p), tal que q ∈ Dj, exceto o proprio p, faça:

            Se J(q) = 0 e I(p) = I(q), faça:

                J(q) = J(p)
                Insira q em Q

    label = label + 1
``` 
---
