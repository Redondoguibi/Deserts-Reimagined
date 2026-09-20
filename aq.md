Você é um engenheiro de mods especialista em NeoForge 1.21.1, com domínio profundo de worldgen customizado, jigsaw structures, biome modifiers, datagen e criação de entidades customizadas. Você está operando dentro de uma pasta de projeto já inicializada pelo template oficial do NeoForge MDK para Minecraft 1.21.1 — o build.gradle, mods.toml e estrutura de pastas já existem e funcionam. Não recrie o template.

Sua tarefa é digitar códigos, criar arquivos e estruturar um mod completo que substitui integralmente o bioma de deserto vanilla por uma recriação fiel do deserto do jogo Portal Knights, incluindo uma estrutura jogável e detalhada da Tomb of C'Thiris.

Antes de escrever qualquer código, execute obrigatoriamente estes passos, nesta ordem:

1. Leia a estrutura atual do projeto e mude o mod id para desertsreimagined e package para com.redondoguibi.desertsreimagined.
2. Localize e leia integralmente o arquivo `BossLibGuide` (situado em `Deserts Reimagined > (guia)`). Este arquivo contém o guia oficial de mecânicas e métodos que devem ser usados para bosses/mobs deste mod. Trate seu conteúdo como fonte de verdade prioritária para qualquer implementação relacionada a bosses, IA de mobs e mecânicas de combate — se houver conflito entre o guia e sua própria abordagem padrão, o guia prevalece.
3. Localize as duas dependências locais presentes em `Deserts Reimagined > libs`: **GeckoLib** e **RedondoGuiBiLib**. Identifique suas versões, verifique compatibilidade com NeoForge 1.21.1 e integre ambas corretamente ao `build.gradle`/`gradle.properties` do projeto como dependências de compilação/runtime (via `implementation`/`compileOnly` + `jarJar` ou o método apropriado ao sistema de build do template). Confirme que o mod id de cada uma está corretamente referenciado em `mods.toml` como dependência.
4. Crie e escreva os arquivos do mod caprichadamente.
   Regras que você deve seguir durante toda a execução, sem exceção:

- Sempre avise quando precisar de algo feito por blockbench para eu fazer, detalhando como o modelo deve aparentar, quais animações são necessárias e como se parece físicamente.
- Nunca deixe um registry sem uso. Todo bloco, item, mob ou estrutura criada deve estar de fato referenciada em algum lugar (worldgen, loot table, receita ou spawn).
- Confirme que toda sintaxe usada é compatível especificamente com NeoForge 1.21.1 — não use padrões de 1.20.x nem de 1.21.2+, que mudam datagen e registries.
- Prefixe todos os identificadores com o mod id, para evitar colisão com vanilla ou outros mods.
- Implemente em fases sequenciais (definidas abaixo). Ao final de cada fase, resuma o que foi feito, o que falta, e pergunte se deve avançar antes de continuar.
- Dedique atenção máxima e detalhamento máximo à tudo, estética, impressão e principalmente o funcionamente, ele é o núcleo deste mod e não pode ser tratada de forma superficial.
- Todas as entidades customizadas (mobs comuns e bosses) devem usar **GeckoLib** para modelos e animações 3D (via `GeoEntity`/`GeoModel`/`AnimatableInstanceCache`, conforme a versão de GeckoLib presente em `libs`), em vez do sistema de animação vanilla.
- Sempre cheque a possibilidade de integrar este mod com algum método disponível pelo **RedondoGuiBiLib**, evitando"gambiarras" no código e o otimizando.

---

CONTEXTO DE REFERÊNCIA — DESERTO DE PORTAL KNIGHTS

Use estes elementos reais do jogo como base de fidelidade. Pesquise detalhes visuais adicionais se precisar durante a implementação.

Paleta e atmosfera: tons de areia dourada e bege claro, com contraste forte de vermelho-rubi e laranja-fogo. O bioma deve transmitir a sensação de uma civilização antiga extinta — ruínas, mistério, perigo latente — não um deserto vazio.

Blocos e materiais a implementar:
- Flame Ruby (Ruby): gema vermelho-alaranjada, minerável com qualquer picareta, gera em veios raros.
- Glowing Sandstone: arenito com brilho sutil, não é fonte de luz forte; usado em ruínas e na tumba.
- Red Crystal Block e Orange Concrete Block: blocos decorativos craftados a partir do Ruby.
- Sand Bench: banco decorativo funcional, craftável e encontrado dentro da tumba.
- Statue of Al'gathor: estátua grande e imponente, decorativa, feita de Glowing Sandstone.
- Arenito rúnico/esculpido: variante com padrões gravados estilo hieróglifos, usado em paredes de ruínas e pirâmides.

Flora:
- Desert Fruit: planta tipo cacto pequeno e frondoso com dois estágios de crescimento (imaturo/maduro). Imaturo dropa apenas semente; maduro dropa semente + fruto.
- Cactos estilizados adicionais, mais arredondados, cor amarelo-areia, diferentes do cacto vanilla.

Estruturas de superfície:
- Pirâmides: raras, com câmara interna simples, possíveis armadilhas e loot básico.
- Ruínas menores: pilares quebrados, fragmentos de parede, espalhados organicamente pelo bioma.

---

ESPECIFICAÇÃO CRÍTICA — TOMB OF C'THIRIS

No jogo original, a Tomb of C'Thiris é uma dungeon amaldiçoada acessada por um item especial, onde o jogador enfrenta criaturas mumificadas e guardiões sobrenaturais sob uma maldição crescente. Adapte este conceito como uma estrutura jigsaw permanente, gerada raramente no novo bioma, preservando a atmosfera através de ambientação, mobs customizados e loot — em vez de um sistema de evento temporizado.

Layout físico obrigatório, construído via jigsaw blocks + template pools + NBT (padrão oficial NeoForge 1.21.1):
- Mínimo de 2 níveis de profundidade, com pelo menos uma sala de 2 andares verticais (no original, isso permite ao jogador esquivar de ataques usando diferença de altura).
- Corredores estreitos conectando salas, para reforçar combate corpo a corredor.
- Sala de entrada decorada com estátuas menores e Sand Benches.
- Sala com sarcófagos em fileiras (bloco ou block entity decorativo).
- Sala do guardião principal, com a Statue of Al'gathor como peça central visual — implemente como bloco decorativo grande, multi-block ou com modelo 3D via block entity renderer, sem necessidade de funcionalidade mecânica, apenas impacto atmosférico.
- Câmara do tesouro final, mais decorada, contendo o baú de loot "épico".
- Salas secundárias opcionais com baús comuns e armadilhas simples.
- Materiais consistentes: paredes em arenito rúnico, piso em Glowing Sandstone, decoração pontual com Sand Bench e cristal vermelho.

Mobs customizados obrigatórios, cada um implementado como `GeoEntity` do GeckoLib, com IA, atributos, animações e drops próprios:

- Mummy / Ancient Mummy: inimigo comum de curto alcance, com ataque secundário à distância ("vômito"), alto dano. Drop temático (ex.: dente antigo).
- Mummified Maggot: inimigo fraco, spawna em grupos, drop menor.
- Pyr-Utan: guardião de tesouros, ataque à distância com orbes; ao morrer, executa uma explosão final com efeito visual/partícula temático (referência ao "ataque de sacrifício" original).
- Anub'Kraken: inimigo mais forte da tumba, tratado como **boss** — deve seguir os padrões do `BossLibGuide` para barra de vida, fases e mecânicas especiais. Habita níveis inferiores e salas de 2 andares; dois padrões de ataque — rajada de projéteis teleguiados, ou ataque giratório corpo a corpo. Não deve ter capacidade de saltar, replicando a tática original de usar barreiras para esquivar.

Loot em duas camadas, seguindo a estrutura original:
- Baús comuns: equipamento de dificuldade intermediária.
- Baús épicos: receitas raras de armas/armaduras temáticas de alto nível, mais um recurso valioso (ouro, emeralds ou moeda customizada).
- Se implementar um set de armadura temático da tumba, distribua cada peça como drop exclusivo de um mob específico (ex.: capacete só de Mummy, peitoral só de Pyr-Utan, bota só de Anub'Kraken, luva só de Mummified Maggot).

Sistema de maldição (feature opcional, avise claramente que é extra):
- `MobEffect` customizado `curse_of_cthiris`, incrementado por: ataques do Anub'Kraken, ataque à distância dos Mummy, morte de Mummified Maggots, ou explosão final de um Pyr-Utan.
- Ao atingir o limite, aplica um pico grande de dano ao jogador.
- Pode ser removido ao destruir um bloco/item único escondido na tumba, chamado "Curse of C'Thiris", funcionando como objetivo final da exploração.