# CHANGE LOG

## 0.3.7
1. Bug fix on `YieldIterator` that caused thread to block forever if `#hasNext()` was called again.
1. use primitive int iterator when possible for performance improvement
 
## 0.3.6
1. Added `InputStreamSupplier#forInputStream( in )` which will parse the given InputStream to determine the encoding
and return a single use `InputStreamSupplier` instance whose get() call can only be called once.

## 0.3.5

1. changed collection of cachedSuppliers from ArrayList to ConcurrentLinkedDeque so we can safely manipulate group concurrently.
1. added `CachedSupplier#ofInitializer()` methods which take Suppliers and return `CachedSupplier<Void>` 
so clients don't have to make Suppliers that return null.