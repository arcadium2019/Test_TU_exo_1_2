package org.example.exo14.repository;

import org.example.exo14.model.Member;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(String id);
}
