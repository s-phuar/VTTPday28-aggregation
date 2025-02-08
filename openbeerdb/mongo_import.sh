#! /usr/bin/env bash

#beers.json
#breweries_geocode.json
#breweries.json
#categories.json
#styles.json

mongoimport beers.json -dopenbeerdb --collection=beers --jsonArray --drop

mongoimport breweries.json -dopenbeerdb --collection=breweries --jsonArray --drop

mongoimport breweries_geocode.json -dopenbeerdb --collection=geocode --jsonArray --drop

mongoimport categories.json -dopenbeerdb --collection=categories --jsonArray --drop

mongoimport styles.json -dopenbeerdb --collection=styles --jsonArray --drop
