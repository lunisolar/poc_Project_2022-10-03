

## POC's Intentional overkills or quirks:

Why 
- I want to have fun.
- This is a bunch of template solutions for some setup elements. Together they might look inconsistent. 

List of overkills/issues: 
- Root project with modules in <code>modules</code> subdirectory.
- buildSrc/src/main/kotlin/project.*-conventions.gradle.kts
- buildSrc/settings.gradle.lts - single source of truth for version numbers.
- Storage is a local TDB2 database (triplestore). This brings a long train of dependencies with lunisolar-lava-rdf libraries.
- Spring WebFlux / Reactive - because I want to check it out. Some first-timer mistakes will be made.
- Despite using reactive mutable objects will be used.  
- Module names does not matter in these examples (not intended for distribution).
- Absolute minimum of validations.
- Absolute minimum of test coverage. 
- Some things are addressed in simple way - e.g. no mitigation for shutdown (e.g. tasks permanently listed as being processed).
- POC vs microservices - it is obvious that solution could be split into two storage/registry (source of truth) and execution (potentially multiple instances).
- Being of POC nature following happened: 
  - I run into issues around AOP/Transaction management vs Reactive API (fixed only on actively used elements - other method of TaskRepository has those beginner mistakes)
  - I run into issues around AOP/DI vs Kotlin classes during initialization (fixed only on actively used elements - other method of TaskRepository has those simple mistakes). 
    And it looks inconsistent (type if DI and annotations).
  - Reactive Schedules sometimes start with error and do not process tasks.