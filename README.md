# forge-camel

Forge 1 plugin helping you to setup project to use apache camel.

## setup
Use `camel setup` to setup the project ( You have to be in an existing project ). This adds the appropriated dependencies and creates a `blueprint.xml` file under `src/main/resources/OSGI-INF/blueprint`. The setup assumes you want to use Java DSL and therefor adds a package node to the camel context which points to the choosen java package.

If you want to use XML DSL just remove the package tag and add your routes to the blueprint.xml file.  

## add camel route builder
After setting up the camel facet by `camel setup` you can add route builders by use of `new-routebuilder --name $classname`

Example: `new-routebuilder --name MyExtraordinaryRouteBuilder`

This will add a java routebuilder to the package defined in the camelContext/package configuration of the blueprint.xml file.
