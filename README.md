# Flight Search Engine Data Analysis Tool

## Introduction
This project encompasses the development of a data analysis tool integrated into an existing flight search engine. The tool processes historical sales data to identify patterns that assist in understanding customer behavior, specifically focusing on families and organized tour groups. The goal is to refine the search engine's capability in offering optimal travel plans based on discovered trends.

## Features
- Data analysis module to process historical sales data
- Integration with existing search engine for enhanced search capabilities
- Hypothesis generation based on customer behavior and preferences
- Performance evaluation of business hypotheses to maximize company profit

## Installation

You have two options for build tools to set up and execute this project: Clojure CLI tools (deps.edn) or Leiningen (Lein).

### Using Leiningen (Recommended)

1. Install Leiningen if you haven't already. Follow the instructions at [Leiningen's official website](https://leiningen.org/#install) for installation.
2. Clone this repository: git clone
3. Change into the project directory: cd flight-search
4. To build and run the project, use Leiningen's commands. For example, to start the application: lein-run

### Using Clojure CLI Tools

1. Install Clojure CLI tools if you haven't already. Follow the instructions at [Clojure's official website](https://clojure.org/guides/getting_started) for installation
2. Clone this repository: git clone
3. Change into the project directory: cd flight-search
4. To build and run the project, use Clojure CLI tools. For example, to start the application: clj -m flight-search.core

For your project, select the build tool that you feel most at ease with or that you prefer.


## The Search Engine

### Input
1. Enter the city of departure.
2. Enter the city of destination.
3. Enter customer data in the correct format (e.g: Jane Doe,2002), new line for each person, and type 'done' when finished:

### Passanger types
* Families (two adults and child/children)
* Organized tour groups

### Search result
* The price decided by the search engine which utilizes a data analysis tool to find the best route possible for your group and it's price. It does this by comparing with other groups in the historical dataset with the same group type and departure and destination cities.

## Usage

Run the data analysis tool and check the namespaces in your enviornment ,modify them if needed. 

## Hypotheses Evaluation
Run the Broker application provided by this respitory to simulate actual sales and evaluate the effectiveness of the hypotheses. Multiple runs with different datasets will help in refining the hypotheses further.

## Contributing

This respitory is not dedicated for exteral use. No pull requests are available.


## Acknowledgements

I would like to express my sincere gratitude to Professor Leonov Evgeny, whose guidance and provision of essential resources such as the datasets and the Broker application were invaluable to the success of this project. Their expertise and support have been a cornerstone of our development process.
