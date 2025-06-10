# Kafka Overview
## 1 - Kafka: Produtores, Consumidores e Streams

### O que é o Kafka e quando usar

Kafka é uma plataforma de **streaming distribuído** desenvolvida pela Apache, usada para processar grandes volumes de dados em tempo real. Ele é ideal para sistemas que precisam lidar com eventos, logs, filas, integração entre microsserviços e pipelines de dados.

**Use Kafka quando:**
- Você precisa de **alta performance** e escalabilidade para mensagens/eventos.
- Os dados devem ser processados **em tempo real ou próximo disso**.
- É necessário **reter mensagens** por um tempo para reprocessamento.
- Vários consumidores precisam acessar os **mesmos dados simultaneamente**.

---

### Alguns conceitos

- **Broker:** Servidor Kafka que recebe, armazena e entrega mensagens. Um cluster Kafka pode ter vários brokers distribuídos.

- **Producer:** Componente que publica mensagens em um tópico Kafka.

- **Consumer:** Componente que lê mensagens de um ou mais tópicos Kafka.

- **Topic:** Canal lógico onde as mensagens são publicadas. Cada tópico pode ter **várias partições**.

  - **Partições:** Subdivisões do tópico que permitem paralelismo. Cada partição mantém a **ordem das mensagens** e é armazenada em disco.

---

### Ciclo de Mensagem

1. Um **producer** envia uma mensagem para um **broker**, publicando em um **tópico específico**.
2. A mensagem é armazenada em uma **partição do tópico**.
3. Um ou mais **consumers** lêem as mensagens do tópico.
4. Cada consumer é responsável por manter o **offset** (posição) da última mensagem consumida.
5. Após o processamento bem-sucedido, o consumer pode fazer um **commit** do offset.
6. Se ocorrer falha, a mensagem pode ser redirecionada para uma **DLQ (Dead Letter Queue)**, se implementada.

---

### Plataforma de Streaming Kafka

- Armazena eventos com base em uma **retenção por tempo configurado**, e **não por consumo**.
- Os eventos são **imutáveis**: uma vez gravados, não são alterados.
- A responsabilidade de rastrear mensagens lidas é do **consumer**.
- **Qualquer consumer** pode acessar uma mensagem, mesmo se já foi lida por outro.
- Kafka é uma **plataforma de streaming distribuída**, escalável horizontalmente.

---

### Diagrama de Fluxo de Mensagem

```txt
           +------------+            +------------+           +-------------+
           |            |            |            |           |             |
           |  Producer  +----------->|   Broker   +----------->|  Consumer   |
           |            |  envia     |  (Topic)   |  entrega   | (leitor de  |
           +------------+  mensagens +------------+  mensagens |   eventos)  |
                                                         |     +-------------+
                                                         |
                                                   +-------------+
                                                   |   Consumer   |
                                                   |  (Analytics) |
                                                   +-------------+

         Mensagens ficam retidas por tempo (ex: 7 dias)
         Offset controlado pelos próprios consumidores
```

---

### Exemplo de uso comum

```txt
[Producer (Sistema A)] → [Kafka (Topic: vendas)] → [Consumer (Sistema B e Sistema C)]
```
- O Sistema A envia eventos de venda.
- O Sistema B processa e salva no banco.
- O Sistema C envia os eventos para um dashboard de BI.

---

### Produtores e Consumidores em Java

- **Produtores** são implementados com `KafkaProducer` e enviam mensagens usando `ProducerRecord`.
- **Consumidores** usam `KafkaConsumer`, assinam tópicos e fazem polling com `poll()`.
- O controle de **offsets** pode ser automático (auto-commit) ou manual.

---



### 🚦 Paralelismo e Competição no Kafka

#### ⚙️ Paralelismo com Partições

- Cada **partição** pode ser processada por **apenas um consumidor dentro de um grupo**.
- Para obter paralelismo real, é preciso:
  - Ter **múltiplas partições no tópico**.
  - Ter **múltiplos consumidores no mesmo grupo**.

Exemplo:
```txt
Tópico: pedidos (3 partições)
Grupo: processamento-pedidos
Consumidores: C1, C2

Resultado:
- Kafka atribui 2 partições para C1, 1 para C2
- Ambos processam mensagens em paralelo
```

#### ⚔️ Competição entre consumidores

- Em um mesmo grupo, os consumidores "competem" pelas partições:
  - Cada partição só pode ser atribuída a **um único consumidor** dentro do grupo.
  - Se um consumidor cair, Kafka redistribui as partições (rebalanceamento).
- Já **entre grupos diferentes**, não há competição:
  - Cada grupo recebe **todas** as mensagens.
  - Isso permite múltiplas equipes/sistemas processarem os mesmos dados de formas diferentes.

---

### 💡 Conclusão visual

```txt
             TÓPICO: PEDIDOS (3 partições)
                     ┌───────────────┐
                     │     Kafka     │
                     └───────────────┘
                           │
          ┌────────────┬───────────────┬─────────────┐
          ▼            ▼               ▼             ▼
     Partição 0    Partição 1     Partição 2     [DLQ opcional]
        │              │               │
    C1 (Grupo A)   C2 (Grupo A)   C3 (Grupo A)
        │              │               │
       BI          Antifraude       Logística

  → Cada consumidor do Grupo A processa 1 partição.
  → Outro grupo (Grupo B) com outros consumidores pode receber TODAS as mensagens também.
```

Essa estrutura permite **escala horizontal**, **isolamento entre sistemas**, e **alto desempenho em tempo real**.

---

### Max Poll e Auto Commit

- `max.poll.records` define quantas mensagens o consumer pega por poll.
- `enable.auto.commit=false` permite controle manual, garantindo que apenas mensagens processadas com sucesso tenham seus offsets confirmados.

---

### Camadas Personalizadas

- Separar o código de Kafka em **camadas próprias** facilita manutenção:
  - `KafkaProducerService`: centraliza o envio de eventos.
  - `KafkaConsumerHandler`: processa mensagens recebidas.
- Isso melhora a organização e facilita testes unitários.

---

### Serialização e Desserialização Customizada

- Kafka usa **serializadores** e **desserializadores** para transformar objetos em bytes.
- Podemos criar **Serializers/Deserializers personalizados** com bibliotecas como **GSON** ou **Jackson** para JSON.

```java
public class PedidoSerializer implements Serializer<Pedido> {
    @Override
    public byte[] serialize(String topic, Pedido data) {
        return new Gson().toJson(data).getBytes();
    }
}
```

---

### DLQ (Dead Letter Queue)

- Mensagens que falham no processamento podem ser redirecionadas para uma fila de erro.
- Evita perda de dados e permite correção posterior.

---

### Microsserviços com Kafka

- Kafka facilita integração entre microsserviços.
- Cada serviço pode ser produtor ou consumidor.
- Microsserviços ficam **desacoplados** e mais fáceis de escalar.

---

### Monorepo e Bibliotecas Comuns

- Em um monorepo, podemos criar **módulos compartilhados**:
  - Ex: `commons-kafka`, `commons-logger`
- Reutiliza código e padroniza uso do Kafka entre os serviços.

---

### Rebalance

### Conclusão

- Kafka é uma solução robusta para mensageria em tempo real.
- Aprendemos a criar produtores e consumidores em Java.
- Estudamos paralelismo, offset, auto-commit e partilhas.
- Implementamos camadas organizadas e serializações customizadas.
- Vimos boas práticas com DLQ, microsserviços e monorepo.

Kafka não é apenas uma fila: é uma plataforma de eventos para arquiteturas modernas.

## 2 – Kafka: Fast Delegate, Evolução e Cluster de Brokers

A seção a seguir aprofunda os conceitos apresentados na trilha **“Fast Delegate, evolução e cluster de brokers”** e mostra como aplicá‑los em um projeto **Spring Boot + Spring for Apache Kafka**.

| #  | Tópico do curso | Conceito na teoria | Tradução prática no Spring |
|----|-----------------|--------------------|---------------------------|
| **2.1 • Single Point of Failure do Broker** | Um cluster com **apenas 1 broker** cai ⇒ toda a plataforma fica fora do ar. | ⚙️ Crie **≥ 2 brokers** e use `replication.factor > 1`. No Spring basta listar todos em `spring.kafka.bootstrap-servers`; o client faz fail‑over automático. |
| **2.2 • Replicação em Cluster** | Cada partição tem **1 líder** e algumas **réplicas** (followers). Se o líder falhar, um follower assume. | No broker: `default.replication.factor=3`, `min.insync.replicas=2`. O driver Spring lê a _metadata_ e continua produzindo/consumindo. |
| **2.3 • Cluster de 5 Brokers, Líderes & Réplicas** | Exemplo real com 5 nós; 3 réplicas/partição, 2 followers sempre *in‑sync*. | No `docker-compose` adicione `kafka1…kafka5`. A app Spring aponta para qualquer broker da lista (`kafka1:9092,…`); o client descobre o resto. |
| **2.4 • Acks & Reliability** | Confirmação de escrita (`acks=0 | 1 | all`), idempotência, retries, timeouts. | Em `application.yml`:<br>`acks=all`, `enable-idempotence=true`, `retries=3`, `delivery.timeout.ms=30000`. |

---

### 2.1 • Single Point of Failure (SPOF) do Broker

| | Descrição |
|---|---|
| **Definição** | Se houver só **um** broker e ele falhar, producers e consumers ficam sem destino. |
| **Sintomas** | Logs: `NOT_LEADER_NOT_AVAILABLE`, latência 100 %, throughput 0. |
| **Correção** | Adicionar brokers e setar `replication.factor > 1`; definir `min.insync.replicas` para consistência. |
| **No Spring** | Múltiplos hosts em `bootstrap-servers`. O client troca de líder sozinho.|

---

### 2.2 • Replicação em Cluster (Líder & Followers)

* **Líder** grava todas as mensagens e responde ao producer.  
* **Followers** replicam o log do líder.  
* **ISR (in‑sync replicas)** = subconjunto de réplicas totalmente atualizadas.  
* Se o líder cair → o controller elege outro nó do ISR como novo líder em milissegundos.

```properties
# No broker
default.replication.factor=3
min.insync.replicas=2
```

---

### 2.3 • Cluster de 5 Brokers

Distribuição (RF = 3):

| Partição | Líder | Follower 1 | Follower 2 |
|----------|-------|------------|------------|
| 0 | **B1** | B3 | B5 |
| 1 | **B2** | B4 | B1 |
| 2 | **B3** | B5 | B2 |
| … | … | … | … |

*Mais nós ⇒ mais throughput e tolerância a falhas (rack awareness).*

---

### 2.4 • Acks & Reliability

| `acks` | Quem confirma | Semântica | Quando usar |
|--------|--------------|-----------|-------------|
| **0** | Ninguém | _At‑most‑once_ | Telemetria não crítica |
| **1** | Líder | _At‑least‑once_ (pode perder se o líder falhar antes do sync) | Default Kafka |
| **all / -1** | Líder + ISR | _At‑least‑once_ + alta durabilidade; com idempotência chega a _exactly‑once_ | Dados financeiros |

#### Configuração recomendada

```yaml
spring:
  kafka:
    producer:
      acks: all
      enable-idempotence: true
      retries: 3
      properties:
        delivery.timeout.ms: 30000
```

---

### Fast Delegation (Fail‑over rápido)

* Nome informal do curso para o **algoritmo de eleição de líder** + atualização instantânea de _metadata_.
* O client Spring se mantém conectado via heartbeats; ao receber nova tabela de partições, retoma o envio ou consumo sem intervenção manual.

---

### Evolução de Cluster

1. **Escalar**: adicionar brokers, alterar partições (`kafka-topics --alter`) e re‑balancear (`kafka-reassign-partitions`).  
2. **Upgrade**: _rolling update_ (um broker por vez) ajustando `inter.broker.protocol.version`.  
3. **Armazenamento**: habilitar Tiered Storage ou compactação por nível.

#### Métricas a observar

* `under-replicated-partitions`  
* `offline-partitions-count`  
* `controller.election-rate`  
* `request-latency-avg`

---

### application-docker.yml – exemplo completo

```yaml
spring:
  kafka:
    bootstrap-servers: kafka1:9092,kafka2:9092,kafka3:9092,kafka4:9092,kafka5:9092
    producer:
      acks: all
      retries: 3
      enable-idempotence: true
      key-serializer: org.apache.kafka.common.serialization.IntegerSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        delivery.timeout.ms: 30000
    admin:
      properties:
        bootstrap.servers: ${spring.kafka.bootstrap-servers}
```

> 💡 **Dica** – use um perfil `local` com `bootstrap-servers: localhost:9092` para desenvolvimento single‑node, e `docker`/`prod` para cluster.

---

### Teste de Fail‑over

1. Com o cluster rodando, execute: `docker compose stop kafka1`.  
2. Observe logs no Spring: breves `WARN … node ‑1 disconnected`, depois normaliza.  
3. Mensagens continuam fluindo; com `acks=all` & `min.insync.replicas≥2` não há perda.

---

**Conclusão**: aplicando as práticas acima — múltiplos brokers, replicação adequada, `acks=all` + idempotência — seu sistema Kafka torna‑se resiliente, escalável e pronto para produção empresarial.
