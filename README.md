
## Intent
Efficient cache result is a high-performance framework for saving cached results.It tries to solve the performance 
problems of some algorithms in high concurrency situations.

## Explanation
It almost does not lock to complete the results cache and get the results.In the case of concurrency, 
when need a result, the result is taken immediately if it has already been calculated.

## Applicability
- You frequently calculate a value
- You want to safely save the results under concurrency conditions
- You do not want to lock when saving results
