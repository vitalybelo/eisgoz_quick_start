package ru.lanit.minobr.service.quick_start.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.minobr.service.quick_start.models.MacTable;

import java.sql.Timestamp;


@Repository
public interface MacTableRepository extends JpaRepository<MacTable, Timestamp> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.mactable WHERE uuid = :uuid", nativeQuery = true)
    void deleteById(String uuid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.mactable set userName = :username, level = :level, category = :category WHERE uuid = :uuid", nativeQuery = true)
    void updateById(String username, String level, String category, String uuid);



}