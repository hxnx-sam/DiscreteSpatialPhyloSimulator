DiscreteSpatialPhyloSimulator
=============================

Individual based modelling code for structured populations including phylogenetic tree outputs.

Introduction
============

The DiscreteSpatialPhyloSimulator is a java program which uses a stochastic individual based model to simulate infection over a structured population.  The individuals are organised into “Demes”, and Demes can be connected together in a variety of ways.  Within a Deme all individuals have the same parameters, but each Deme could have its own particular parameters.  For example, the DiscreteSpatialPhyloSimulator can be configured for:
* Phylogeography – each Deme is a location, individuals may migrate between locations.
* Zoonoses – each Deme is a species, infections may be transmitted between Demes, but the hosts themselves do not move.
* Networks – each Deme contains one host, infections may be transmitted between neighbouring Demes.

Code, Test Files & Documentation
================================

The DiscreteSpatialPhyloSimulator is written in Java (Eclipse project).
The Test folder contains example input XMLs used for testing and the Validation folder contains some simple examples cross checked against results generated in R.
The Documentation folder contains a short overview of this code.
