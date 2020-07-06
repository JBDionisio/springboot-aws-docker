package com.dionisio.beerstore.repository;

import com.dionisio.beerstore.model.Beer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Beers extends JpaRepository<Beer, Long> {

}