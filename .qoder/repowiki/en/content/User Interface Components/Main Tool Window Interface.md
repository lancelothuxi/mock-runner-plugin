# Main Tool Window Interface

<cite>
**Referenced Files in This Document**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java)
- [RunnerToolWindowFactory.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/RunnerToolWindowFactory.java)
- [JsonTableCellRenderer.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellRenderer.java)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java)
- [InlineJsonEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/InlineJsonEditor.java)
- [MockConfigDialog.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockConfigDialog.java)
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction
This document describes the main Mock Runner tool window interface, focusing on the MockRunnerToolWindowContent class as the central UI component. It explains the BorderLayout-based layout with a top toolbar, center scrollable table, and bottom pagination controls. It documents the custom MockTableModel with six columns (Enabled, Class, Method, Args, Mode, Value), table sorting and filtering via TableRowSorter, regex-based search, pagination with configurable page size, global enable/disable toggling, dirty state tracking for unsaved changes, and integration with MockConfigService for data persistence. Accessibility and user interaction patterns are also covered.

## Project Structure
The tool window integrates with IntelliJ Platform’s tool window system. The content panel is registered as a tool window content via RunnerToolWindowFactory and managed by MockRunnerToolWindowContent. Data is persisted and synchronized through MockConfigService, which maintains MockConfig and MockMethodConfig models.

```mermaid
graph TB
Factory["RunnerToolWindowFactory<br/>Registers tool window content"] --> Content["MockRunnerToolWindowContent<br/>Main UI controller"]
Content --> Model["MockTableModel<br/>Custom table model"]
Content --> Sorter["TableRowSorter<br/>Sorting and filtering"]
Content --> Renderer["JsonTableCellRenderer<br/>JSON rendering"]
Content --> Editor["JsonTableCellEditor<br/>JSON editing"]
Content --> Service["MockConfigService<br/>Persistence and sync"]
Service --> Config["MockConfig<br/>Mock rules"]
Service --> MethodConfig["MockMethodConfig<br/>Individual method configs"]
```

**Diagram sources**
- [RunnerToolWindowFactory.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/RunnerToolWindowFactory.java#L10-L20)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L22-L160)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L23-L40)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java#L12-L218)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java#L5-L94)

**Section sources**
- [RunnerToolWindowFactory.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/RunnerToolWindowFactory.java#L10-L20)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L46-L160)

## Core Components
- MockRunnerToolWindowContent: Central UI controller managing layout, toolbar actions, table model, sorting/filtering, pagination, global toggle, dirty state, and persistence integration.
- MockTableModel: Custom AbstractTableModel implementing six columns and editable cells for Enabled, Mode, and Value.
- TableRowSorter: Enables sorting and regex-based filtering across all columns.
- JsonTableCellRenderer/JsonTableCellEditor: Advanced JSON rendering and inline editing with syntax highlighting and validation.
- MockConfigService: Persists configuration to XML state and a temporary JSON file for runtime agent consumption, and updates UI on changes.
- MockConfig/MockMethodConfig: Data models representing mock rules and per-method configuration.

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L22-L160)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L341-L447)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L23-L96)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java#L12-L86)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java#L5-L94)

## Architecture Overview
The tool window follows a layered pattern:
- UI Layer: MockRunnerToolWindowContent orchestrates layout, events, and persistence triggers.
- Model Layer: MockTableModel exposes data from MockConfigService.getConfig().
- Persistence Layer: MockConfigService persists to project state and a temporary JSON file.
- Rendering/Editing Layer: JsonTableCellRenderer and JsonTableCellEditor provide rich JSON editing and display.

```mermaid
sequenceDiagram
participant User as "User"
participant UI as "MockRunnerToolWindowContent"
participant Table as "JBTable"
participant Model as "MockTableModel"
participant Service as "MockConfigService"
User->>UI : Click "Save"
UI->>Service : saveConfig()
Service->>Service : Persist to XML state and temp JSON
Service-->>UI : State updated
UI->>Model : fireTableDataChanged()
Model-->>Table : Repaint with new data
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L110-L116)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L41-L58)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L341-L447)

## Detailed Component Analysis

### Layout and Controls (BorderLayout)
- Top panel: Search field with real-time regex filtering and global enable/disable toggle.
- Center panel: Scrollable JBTable displaying mock configurations.
- Bottom panel: Previous/Next buttons and page label for pagination.

```mermaid
flowchart TD
Root["BorderLayout Root Panel"] --> North["Top Toolbar Panel"]
Root --> Center["Scrollable JBTable"]
Root --> South["Pagination Panel"]
North --> Search["JBTextField 'Search'"]
North --> GlobalToggle["Button 'Disable All'/'Enable All'"]
North --> Toolbar["Save/Clear/Refresh + Stats"]
Center --> Table["JBTable with MockTableModel"]
South --> Prev["Previous Button"]
South --> Page["Page Label"]
South --> Next["Next Button"]
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L48-L150)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L48-L150)

### MockTableModel (Six Columns)
Columns: Enabled, Class, Method, Args, Mode, Value
- Enabled: Boolean, editable; toggles method enablement.
- Class: Short class name display.
- Method: Method name.
- Args: Method signature.
- Mode: Dropdown with "Return Value" or "Exception".
- Value: JSON editor for return value or exception info.

```mermaid
classDiagram
class MockTableModel {
+setMockMethods(methods)
+getRowCount() int
+getColumnCount() int
+getColumnName(col) String
+getColumnClass(col) Class
+isCellEditable(row,col) boolean
+getValueAt(row,col) Object
+setValueAt(value,row,col) void
+getMethodAt(rowIndex) MockMethodConfig
}
class MockMethodConfig {
+boolean enabled
+String className
+String methodName
+String signature
+String returnValue
+String returnType
+boolean throwException
+String exceptionType
+String exceptionMessage
+setEnabled(bool)
+setThrowException(bool)
+setReturnValue(str)
+setExceptionType(str)
+setExceptionMessage(str)
}
MockTableModel --> MockMethodConfig : "manages list of"
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L341-L447)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java#L5-L94)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L341-L447)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java#L5-L94)

### Sorting and Filtering with TableRowSorter
- Real-time filtering via DocumentListener on the search field.
- Regex-based filtering applied to all visible rows.
- Case-insensitive matching using a regex filter.

```mermaid
flowchart TD
Start(["User types in Search"]) --> GetText["Get trimmed text"]
GetText --> IsEmpty{"Text empty?"}
IsEmpty --> |Yes| NoFilter["Remove RowFilter"]
IsEmpty --> |No| BuildRegex["Build '(?i)' + text"]
BuildRegex --> ApplyFilter["Apply regex RowFilter"]
NoFilter --> UpdateStats["Update stats label"]
ApplyFilter --> UpdateStats
UpdateStats --> End(["Table revalidated"])
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L89-L96)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L173-L181)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L55-L57)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L89-L96)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L173-L181)

### Pagination System
- Fixed page size constant.
- Current page index tracked locally.
- Navigation updates page label and enables/disables buttons.
- Table model refreshes on navigation.

```mermaid
flowchart TD
Init["Initialize PAGE_SIZE=20, currentPage=0"] --> Load["Load mock configs"]
Load --> CalcPages["totalPages = ceil(total/PAGE_SIZE)"]
CalcPages --> UpdateUI["Update page label and button states"]
UpdateUI --> Nav{"User clicks Prev/Next"}
Nav --> |Prev| Dec["currentPage--"] --> Refresh["fireTableDataChanged()"] --> UpdateUI
Nav --> |Next| Inc["currentPage++"] --> Refresh --> UpdateUI
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L31-L36)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L228-L255)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L245-L248)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L31-L36)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L228-L255)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L245-L248)

### Global Enable/Disable Toggle
- Single button switches all mock methods’ enabled state.
- Updates UI immediately and marks dirty.
- Persists changes on Save.

```mermaid
sequenceDiagram
participant User as "User"
participant UI as "MockRunnerToolWindowContent"
participant Service as "MockConfigService"
participant Config as "MockConfig"
User->>UI : Click "Disable All"/"Enable All"
UI->>UI : Toggle globalEnabled flag
UI->>Service : Get config
Service-->>UI : MockConfig
UI->>Config : Iterate methods and set enabled
UI->>UI : markDirty(), fireTableDataChanged()
UI->>UI : updateStats()
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L183-L197)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L37-L39)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L183-L197)

### Dirty State Tracking and Save Behavior
- Dirty flag toggled when edits occur.
- Save button enabled only when dirty.
- Save writes to persistent state and clears dirty flag.

```mermaid
flowchart TD
Edit["Cell edited"] --> MarkDirty["markDirty(): dirty=true, enable Save"]
SaveClick["Click Save"] --> Persist["MockConfigService.saveConfig()"]
Persist --> ClearDirty["clearDirty(): dirty=false, disable Save"]
ClearDirty --> Done["UI updated"]
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L296-L306)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L110-L116)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L41-L58)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L296-L306)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L110-L116)

### Integration with MockConfigService (Persistence)
- Loads initial data from MockConfigService.getConfig().
- On Save, persists to XML state and a temporary JSON file for agent consumption.
- On load, rebuilds mock rules and updates UI.

```mermaid
sequenceDiagram
participant UI as "MockRunnerToolWindowContent"
participant Service as "MockConfigService"
participant Config as "MockConfig"
UI->>Service : getConfig()
Service-->>UI : MockConfig
UI->>Config : setMockMethods(list)
UI->>Service : saveConfig()
Service->>Service : Write XML state + temp JSON
Service->>Service : rebuildMockRules()
Service->>UI : updateToolWindowFromConfig()
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L166-L171)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L37-L58)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L98-L107)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L166-L171)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L37-L58)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L98-L107)

### JSON Editing Experience
- Value column uses JsonTableCellRenderer for formatted display with syntax highlighting.
- JsonTableCellEditor opens a modal editor supporting:
  - JSON editing with InlineJsonEditor (format/validate).
  - Exception editing with separate form parsing "Type: message".

```mermaid
sequenceDiagram
participant User as "User"
participant Table as "JBTable"
participant Editor as "JsonTableCellEditor"
participant Inline as "InlineJsonEditor"
User->>Table : Double-click Value cell
Table->>Editor : getTableCellEditorComponent()
Editor->>Editor : Determine mode (Return/Exception)
alt Return Value mode
Editor->>Inline : createLarge(project, value)
Inline-->>Editor : Editor component
else Exception mode
Editor->>Editor : Show exception form
end
Editor-->>Table : fireEditingStopped()
Table->>Model : setValueAt(newValue)
Model->>UI : markDirty(), updateStats()
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L73-L76)
- [JsonTableCellRenderer.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellRenderer.java#L75-L110)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L75-L92)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L94-L157)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L159-L261)
- [InlineJsonEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/InlineJsonEditor.java#L34-L58)

**Section sources**
- [JsonTableCellRenderer.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellRenderer.java#L14-L110)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L75-L92)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L94-L157)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L159-L261)
- [InlineJsonEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/InlineJsonEditor.java#L34-L58)

### Additional UI Patterns
- MockConfigDialog provides a project-wide configuration experience with smart mock value generation and JSON editing.
- MockValueGenerator creates representative JSON for complex return types.

**Section sources**
- [MockConfigDialog.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockConfigDialog.java#L27-L108)
- [MockConfigDialog.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockConfigDialog.java#L110-L160)
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java#L18-L100)

## Dependency Analysis
The UI depends on the service layer for data and persistence. The service layer depends on data models for configuration representation and rebuilds mock rules for runtime use.

```mermaid
graph TB
UI["MockRunnerToolWindowContent"] --> Service["MockConfigService"]
Service --> Config["MockConfig"]
Config --> Rule["MockConfig.MockRule"]
Config --> Method["MockMethodConfig"]
UI --> Table["JBTable + MockTableModel"]
Table --> Renderer["JsonTableCellRenderer"]
Table --> Editor["JsonTableCellEditor"]
Editor --> Inline["InlineJsonEditor"]
```

**Diagram sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L22-L160)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L23-L96)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java#L12-L86)
- [MockMethodConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockMethodConfig.java#L5-L94)

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L22-L160)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L23-L96)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java#L12-L86)

## Performance Considerations
- Column width initialization deferred until after table creation to avoid layout overhead.
- JSON rendering uses caching for formatted strings to reduce repeated computation.
- Pagination limits visible rows per page to manage large datasets efficiently.
- Filtering uses regex with case-insensitive flag; keep search terms concise for responsiveness.

[No sources needed since this section provides general guidance]

## Troubleshooting Guide
- Unsaved changes prompt: When navigating away, the UI prompts to save, discard, or cancel. Use Save to persist, Discard to proceed without saving, or Cancel to stay.
- Clear All confirmation: Requires explicit confirmation; unsaved changes are handled before clearing.
- Refresh conflicts: If unsaved changes exist, refresh prompts to save or discard before reloading data.
- JSON validation: Inline JSON editor validates syntax; invalid JSON prevents saving.
- Tool window visibility: Adding mock methods ensures the tool window is shown if not visible.

**Section sources**
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L199-L226)
- [MockRunnerToolWindowContent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/MockRunnerToolWindowContent.java#L312-L334)
- [JsonTableCellEditor.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/JsonTableCellEditor.java#L124-L139)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L130-L137)

## Conclusion
The Mock Runner tool window provides a robust, editable interface for managing mock configurations. Its BorderLayout layout cleanly separates concerns, the custom table model offers precise control over data presentation and editing, and the integration with MockConfigService ensures reliable persistence and synchronization. Advanced JSON editing, filtering, sorting, and pagination deliver a productive developer experience while maintaining accessibility and usability.