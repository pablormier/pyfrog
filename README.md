# Pyfrog

A Py4j gateway for [Frog](https://github.com/citiususc/frog).

### Usage

Download the bundled Pyfrog jar from the [releases](https://github.com/pablormier/pyfrog/releases) page and run it in order to launch a socket-based gateway:

`java -jar pyfrog-1.0-bundle.jar`

Once the application is running, you can use Py4j for Python to connect to the gateway. Here is a complete example:

```python
from py4j.java_gateway import JavaGateway, GatewayParameters
jar_path = 'lib/pyfrog-1.0-bundle.jar'

# Run the jar (you can skip this by manually running the jar)
import subprocess
subprocess.Popen(['java','-jar', jar_path])

# auto_convert=True to convert from python lists to java lists
gateway = JavaGateway(auto_convert=True)

# Load a FRULER KB
kb = gateway.entry_point.loadKb('model.kb')

database = kb.getKnowledgeBase().getDatabase()

# Print the names of the variables in the model
for var in database.getInputs():
    print(var.getName())
    
# Inference
kb.denormalizedInference([var1, var2, var3,...])
```
