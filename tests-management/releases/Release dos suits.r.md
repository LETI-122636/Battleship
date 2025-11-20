# Release dos suits

## Test Suite: Relatórios e Testes Unitários

### S1 . Escopo
*  Executar testes unitários do projecto e recolher resultados para relatórios.
    tags: #unit-tests #reports #checklists

*  Gerar relatórios Allure a partir dos `allure-results` (quando configurado).
    tags: #unit-tests #reports #checklists

*  Gerar relatório de cobertura JaCoCo.
    tags: #unit-tests #reports #checklists

*  (Recomendado) Incluir também testes de integração como operação separada (ver plugin Failsafe).
    tags: #unit-tests #reports #checklists


### S2 . Classes de teste (referência)
*  BargeTest  (TMS-ID: TMS-UT-002-01)
    tags: #unit-tests #reports #checklists

*  CaravelTest  (TMS-ID: TMS-UT-002-02)
    tags: #unit-tests #reports #checklists

*  CarrackTest  (TMS-ID: TMS-UT-002-03)
    tags: #unit-tests #reports #checklists

*  CompassTest  (TMS-ID: TMS-UT-002-04)
    tags: #unit-tests #reports #checklists

*  CompassTests  (TMS-ID: TMS-UT-002-05)
    tags: #unit-tests #reports #checklists

*  ConditionCoverageTest  (TMS-ID: TMS-UT-002-06)
    tags: #unit-tests #reports #checklists

*  FleetConditionTest  (TMS-ID: TMS-UT-002-07)
    tags: #unit-tests #reports #checklists

*  FleetEdgeCasesTest  (TMS-ID: TMS-UT-002-08)
    tags: #unit-tests #reports #checklists

*  FleetTest  (TMS-ID: TMS-UT-002-09)
    tags: #unit-tests #reports #checklists

*  FleetTests  (TMS-ID: TMS-UT-002-10)
    tags: #unit-tests #reports #checklists

*  FrigateTest  (TMS-ID: TMS-UT-002-11)
    tags: #unit-tests #reports #checklists

*   GalleonTest  (TMS-ID: TMS-UT-002-12)
    tags: #unit-tests #reports #checklists

*  GameBranchCoverageTests  (TMS-ID: TMS-UT-002-13)
    tags: #unit-tests #reports #checklists

*   GameTest  (TMS-ID: TMS-UT-002-14)
    tags: #unit-tests #reports #checklists

*   GameTests  (TMS-ID: TMS-UT-002-15)
    tags: #unit-tests #reports #checklists

*   PositionTest  (TMS-ID: TMS-UT-002-16)
    tags: #unit-tests #reports #checklists

*  ShipConstructionTests1  (TMS-ID: TMS-UT-002-17)
    tags: #unit-tests #reports #checklists

*  ShipEdgeCasesTest  (TMS-ID: TMS-UT-002-18)
    tags: #unit-tests #reports #checklists

*   ShipTest  (TMS-ID: TMS-UT-002-19)
    tags: #unit-tests #reports #checklists

*   ShipTests  (TMS-ID: TMS-UT-002-20)
    tags: #unit-tests #reports #checklists


### S3 . Pré-requisitos
* Java 17+ instalado
    tags: #unit-tests #reports #checklists

* Maven instalado ou usar o "Reload Maven Projects" do IDE
    tags: #unit-tests #reports #checklists

* (Opcional) Allure CLI instalado para visualização local dos relatórios
    tags: #unit-tests #reports #checklists

* Se for executar testes de integração: serviços necessários disponíveis ou uso de Testcontainers
    tags: #unit-tests #reports #checklists


### S4 . Passos operacionais (execução local)
* Executar testes unitários e gerar resultados:
    tags: #unit-tests #reports #checklists

* Gerar relatório JaCoCo (se configurado):
    tags: #unit-tests #reports #checklists

* Gerar/servir relatório Allure (se `target/allure-results` existir):
    tags: #unit-tests #reports #checklists

* (Opcional) Executar testes de integração com Failsafe:
    tags: #unit-tests #reports #checklists


### S5 . Critérios de aceitação
* Testes unitários executam sem erros de configuração.
    tags: #unit-tests #reports #checklists

* `target/allure-results` é criado quando os testes produzem resultados integráveis com Allure.
    tags: #unit-tests #reports #checklists

* `target/site/jacoco/index.html` existe após execução do JaCoCo.
    tags: #unit-tests #reports #checklists


### S6 . Checklist rápido
* Código compilado
    tags: #unit-tests #reports #checklists

* Testes unitários executados
    tags: #unit-tests #reports #checklists

* `target/allure-results` criado (quando aplicável)
    tags: #unit-tests #reports #checklists

* Relatórios Allure/JaCoCo gerados
    tags: #unit-tests #reports #checklists
    Versão: 1.0
    Última atualização: 2025-11-20


### Caso separado: Tarefas
* TasksTests  (TMS-ID: TMS-UT-002-21)
    tags: #unit-tests #reports #checklists


## Test_suit

### Caso separado: Tarefas
* TasksTests (TMS-ID: TMS-UT-002-21)
    tags: #unit-tests #test-cases
    --
    Versão: 1.0
    Última atualização: 2025-11-20


### Casos de teste (unitários)
* BargeTest (TMS-ID: TMS-UT-002-01)
    tags: #unit-tests #test-cases

* CaravelTest (TMS-ID: TMS-UT-002-02)
    tags: #unit-tests #test-cases

* CarrackTest (TMS-ID: TMS-UT-002-03)
    tags: #unit-tests #test-cases

* CompassTest (TMS-ID: TMS-UT-002-04)
    tags: #unit-tests #test-cases

* CompassTests (TMS-ID: TMS-UT-002-05)
    tags: #unit-tests #test-cases

* ConditionCoverageTest (TMS-ID: TMS-UT-002-06)
    tags: #unit-tests #test-cases

* FleetConditionTest (TMS-ID: TMS-UT-002-07)
    tags: #unit-tests #test-cases

* FleetEdgeCasesTest (TMS-ID: TMS-UT-002-08)
    tags: #unit-tests #test-cases

* FleetTest (TMS-ID: TMS-UT-002-09)
    tags: #unit-tests #test-cases

* FleetTests (TMS-ID: TMS-UT-002-10)
    tags: #unit-tests #test-cases

* FrigateTest (TMS-ID: TMS-UT-002-11)
    tags: #unit-tests #test-cases

* GalleonTest (TMS-ID: TMS-UT-002-12)
    tags: #unit-tests #test-cases

* GameBranchCoverageTests (TMS-ID: TMS-UT-002-13)
    tags: #unit-tests #test-cases

* GameTest (TMS-ID: TMS-UT-002-14)
    tags: #unit-tests #test-cases

* GameTests (TMS-ID: TMS-UT-002-15)
    tags: #unit-tests #test-cases

* PositionTest (TMS-ID: TMS-UT-002-16)
    tags: #unit-tests #test-cases

* ShipConstructionTests1 (TMS-ID: TMS-UT-002-17)
    tags: #unit-tests #test-cases

* ShipEdgeCasesTest (TMS-ID: TMS-UT-002-18)
    tags: #unit-tests #test-cases

* ShipTest (TMS-ID: TMS-UT-002-19)
    tags: #unit-tests #test-cases

* ShipTests (TMS-ID: TMS-UT-002-20)
    tags: #unit-tests #test-cases


