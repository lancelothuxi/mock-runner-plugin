# Changelog

All notable changes to the Mock Runner plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.5] - 2026-02-16

### Added
- Exception mocking mode - methods can now throw exceptions instead of returning values
- Mode column in table to switch between "Return Value" and "Exception"
- Exception configuration dialog with type and message fields
- Smart cell editor that adapts based on mode (JSON editor vs exception form)

### Changed
- Refactored package name from `com.example.plugin` to `io.github.lancelothuxi.idea.plugin.mock`
- Improved type parsing using `Method.getGenericReturnType()` for 100% accuracy
- Removed verbose debug logging for production release
- Cleaned up all System.out.println statements

### Fixed
- Generic type parsing now correctly handles `List<Student>` without ClassCastException
- Agent now uses actual Method reflection Type instead of string parsing
- Removed duplicate code in MockAgent

### Removed
- All debug and test files from repository
- Unnecessary logging that exposed internal details to users

## [1.0.4] - 2026-02-15

### Added
- Pagination support (20 items per page)
- Real-time search functionality
- Confirmation dialog for "Clear All" action
- Individual enable/disable toggles for each mock
- Global enable/disable functionality
- Statistics display showing total and enabled mocks

### Changed
- Enhanced table UI with better column sizing
- Improved MockRunnerToolWindowContent with custom table model

### Fixed
- Duplicate mock method entries bug
- UI refresh logic to prevent double addition

## [1.0.3] - 2026-02-14

### Added
- Inline JSON editor with syntax highlighting
- Modern popup-based editing interface
- JsonTableCellEditor and JsonTableCellRenderer
- AddMockDialog with professional UI

### Changed
- Replaced simple input boxes with IntelliJ's native Editor components
- Improved mock data generation for generic types

### Fixed
- JSON editing now supports large and complex objects
- Auto-formatting and validation for JSON values

## [1.0.2] - 2026-02-13

### Added
- Intelligent mock data generation
- Support for generic types like `List<Student>`
- Enhanced MockValueGenerator

### Changed
- Improved default mock values to include actual object properties
- Better handling of complex return types

## [1.0.1] - 2026-02-12

### Added
- MockRunner and MockDebugRunner for execution
- Tool window for managing mock configurations
- Line marker provider for visual indicators

### Fixed
- Agent jar packaging issues
- Configuration persistence

## [1.0.0] - 2026-02-11

### Added
- Initial release
- Basic method mocking functionality
- Java Agent integration using ByteBuddy
- Visual configuration UI
- Mock configuration persistence
- Right-click context menu action
- Gutter icon for mockable methods

### Features
- Runtime method interception
- JSON-based mock value configuration
- Enable/disable individual mocks
- Project-level configuration storage

---

## Legend

- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for now removed features
- `Fixed` for any bug fixes
- `Security` for vulnerability fixes
