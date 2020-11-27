# CHANGE LOG

## 0.3.5

1. changed collection of cachedSuppliers from ArrayList to ConcurrentLinkedDeque so we can safely manipulate group concurrently.
1. added `CachedSupplier#ofInitializer()` methods which take Suppliers and return `CachedSupplier<Void>` 
so clients don't have to make Suppliers that return null.