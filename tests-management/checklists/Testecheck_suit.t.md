# Test Suite: Relatórios e Testes Unitários

Tags: #unit-tests #reports #checklists

ID: TS-CHK-001
TMS-ID: TMS-TS-CHK-001

Propósito: Listar o tipo de operações a executar na diretoria "checklists" — geração de relatórios (Allure, JaCoCo) e execução de testes automáticos do tipo unitário. O ficheiro serve também como referência das classes de teste existentes para executar/regressar relatórios.

## 1. Escopo
- Executar testes unitários do projecto e recolher resultados para relatórios.
- Gerar relatórios Allure a partir dos `allure-results` (quando configurado).
- Gerar relatório de cobertura JaCoCo.
- (Recomendado) Incluir também testes de integração como operação separada (ver plugin Failsafe).

## 2. Classes de teste (referência)
As seguintes classes de teste unitário existem em `src/test/java/iscteiul/ista/battleship` e devem ser consideradas nas execuções:

* BargeTest  (TMS-ID: TMS-UT-002-01)
* CaravelTest  (TMS-ID: TMS-UT-002-02)
* CarrackTest  (TMS-ID: TMS-UT-002-03)
* CompassTest  (TMS-ID: TMS-UT-002-04)
* CompassTests  (TMS-ID: TMS-UT-002-05)
* ConditionCoverageTest  (TMS-ID: TMS-UT-002-06)
* FleetConditionTest  (TMS-ID: TMS-UT-002-07)
* FleetEdgeCasesTest  (TMS-ID: TMS-UT-002-08)
* FleetTest  (TMS-ID: TMS-UT-002-09)
* FleetTests  (TMS-ID: TMS-UT-002-10)
* FrigateTest  (TMS-ID: TMS-UT-002-11)
* GalleonTest  (TMS-ID: TMS-UT-002-12)
* GameBranchCoverageTests  (TMS-ID: TMS-UT-002-13)
* GameTest  (TMS-ID: TMS-UT-002-14)
* GameTests  (TMS-ID: TMS-UT-002-15)
* PositionTest  (TMS-ID: TMS-UT-002-16)
* ShipConstructionTests1  (TMS-ID: TMS-UT-002-17)
* ShipEdgeCasesTest  (TMS-ID: TMS-UT-002-18)
* ShipTest  (TMS-ID: TMS-UT-002-19)
* ShipTests  (TMS-ID: TMS-UT-002-20)


## Caso separado: Tarefas
* TasksTests  (TMS-ID: TMS-UT-002-21)

## 3. Pré-requisitos
- Java 17+ instalado
- Maven instalado ou usar o "Reload Maven Projects" do IDE
- (Opcional) Allure CLI instalado para visualização local dos relatórios
- Se for executar testes de integração: serviços necessários disponíveis ou uso de Testcontainers

## 4. Passos operacionais (execução local)
1. Executar testes unitários e gerar resultados:

```powershell
mvn -DskipTests=false test
```

2. Gerar relatório JaCoCo (se configurado):

```powershell
mvn test jacoco:report
```

3. Gerar/servir relatório Allure (se `target/allure-results` existir):

```powershell
# servir interativamente (Allure CLI)
allure serve target/allure-results

# gerar relatório estático
allure generate target/allure-results -o target/allure-report --clean
```

4. (Opcional) Executar testes de integração com Failsafe:

```powershell
mvn verify
```

## 5. Critérios de aceitação
- Testes unitários executam sem erros de configuração.
- `target/allure-results` é criado quando os testes produzem resultados integráveis com Allure.
- `target/site/jacoco/index.html` existe após execução do JaCoCo.

## 6. Checklist rápido
- [ ] Código compilado
- [ ] Testes unitários executados
- [ ] `target/allure-results` criado (quando aplicável)
- [ ] Relatórios Allure/JaCoCo gerados

Versão: 1.0
Última atualização: 2025-11-20
