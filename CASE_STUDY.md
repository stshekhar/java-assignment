# Case Study Scenarios to discuss

## Before defining the scope
Before getting into solutions, I’d clarify which costs matter most, where the cost data comes from, and who will use it to make decisions. I’d also agree on what success looks like—such as better cost visibility without reducing service quality. That gives us a practical starting scope.

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating fulfillment costs, drawing on relevant experience and raising important questions.

**Questions you may have and considerations:**
I’d first agree on what counts as a cost and how shared expenses are allocated. Labor or transport may be linked directly to a location, while rent and overhead need a consistent rule—such as space used or order volume. I’d check that the underlying data is complete and avoid comparing figures calculated in different ways.

The key questions are: Which costs should we track, what is the source of truth, and what allocation rules will finance and operations trust? In my experience, clear definitions and reliable data matter just as much as the reporting tool.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss cost optimization strategies, their expected outcomes, and how you would prioritize and implement them.

**Questions you may have and considerations:**
I’d establish a reliable cost baseline, then look for the biggest opportunities—for example, improving labor or space use, reducing unnecessary transfers, or consolidating shipments. I’d prioritize changes by likely savings, effort, and impact on service. A small pilot can show whether the savings are real; I’d track them alongside delivery times and order accuracy before expanding the change.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
Connecting the tool to financial systems can reduce manual reconciliation and help teams spot cost variances sooner. I’d first agree which system owns each piece of data and how locations map to financial cost centers. I’d also set realistic expectations for “real time,” since some costs are only final after accounting updates.

To keep the integration dependable, I’d validate incoming data, prevent duplicate updates, and make failures visible so they can be corrected. I’d start with the most valuable data exchange and reconcile its results with finance before expanding.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
Budgets set spending expectations; forecasts help teams adjust as demand and costs change. I’d combine historical spending with key drivers such as order volume, seasonality, staffing, and transport rates. The tool should make assumptions visible, compare actuals with budget, and allow a few useful scenarios. Forecasts are estimates, so I’d track their accuracy and update them as conditions change.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
Because the new warehouse reuses the old Business Unit Code, we need another way—such as each warehouse’s record ID and dates—to keep their costs separate. I’d preserve the old warehouse’s history, record the new warehouse’s costs from its opening date, and identify one-time transition expenses like moving stock or overlapping rent.

That lets the business compare the new operation with the old one fairly, keep an audit trail, and check whether the replacement is meeting its budget without mixing the two facilities’ costs.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
