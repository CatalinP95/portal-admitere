package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Integer> {

    List<Block> findByNameAndEnabled(String name, int enabled);

    Optional<Block> findFirstByNameAndEnabled(String name, int enabled);

    List<Block> findByEnabled(int enabled);

    @Query(value =
            "select block.name, count(blockrequest.id) as number from blockrequest " +
            "right join block on block.id = blockrequest.block_id " +
            "GROUP BY block.id", nativeQuery = true)
    List<Object[]> countRequestsPerBlock();

    @Query(value =
            "select block.name, count(rentalagreement.id) as number from rentalagreement " +
            "inner join blockrequest on blockrequest.id = rentalagreement.blockrequest_id " +
            "right join block on block.id = blockrequest.block_id " +
            "GROUP BY block.name", nativeQuery = true)
    List<Object[]> countStudentsPerBlock();
}
