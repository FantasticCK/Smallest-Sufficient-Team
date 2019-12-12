package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {

    }
}

// DFS + Prune + Bit Mask
class Solution {
    List<Integer> sol = new ArrayList<>();

    public int[] smallestSufficientTeam(String[] req_skills, List<List<String>> people) {
        Map<String, Integer> idx = new HashMap<>();
        int n = 0;
        for (String s : req_skills) idx.put(s, n++);///skills are represented by 0, 1, 2....
        int[] pe = new int[people.size()];
        for (int i = 0; i < pe.length; i++) {
            for (String p : people.get(i)) {
                int skill = idx.get(p);
                pe[i] += 1 << skill;
            }
        } // each person is transferred to a number, of which the bits of 1 means the guy has the skill
        boolean[] isBannedArr = calculateDuplicate(pe, req_skills.length);
        search(0, pe, new ArrayList<Integer>(), n, isBannedArr);
        int[] ans = new int[sol.size()];
        for (int i = 0; i < sol.size(); i++) ans[i] = sol.get(i);
        return ans;
    }

    public void search(int cur, int[] pe, List<Integer> oneSol, int n, boolean[] isBannedArr) {
        if (cur == (1 << n) - 1) {  // when all bits are 1, all skills are covered
            if (sol.size() == 0 || oneSol.size() < sol.size()) {
                sol = new ArrayList<>(oneSol);
            }
            return;
        }
        if (sol.size() != 0 && oneSol.size() >= sol.size()) return;    //pruning

        int zeroBit = 0;
        while (((cur >> zeroBit) & 1) == 1) zeroBit++;
        for (int i = 0; i < pe.length; i++) {
            if (isBannedArr[i]) continue;
            int per = pe[i];
            if (((per >> zeroBit) & 1) == 1) {
                oneSol.add(i); // when a person can cover a zero bit in the current number, we can add him
                search(cur | per, pe, oneSol, n, isBannedArr);
                oneSol.remove(oneSol.size() - 1);  //search in a backtracking way
            }
        }
    }

    private boolean[] calculateDuplicate(int[] peopleBinary, int len) {
        boolean[] isDuplicateArr = new boolean[peopleBinary.length];
        //compare people[i] and people[j], if skill_i > skill_j means people_i have at least one skill people_j doesn't have, and vice versa
        for (int i = 0; i < peopleBinary.length; i++) {
            for (int j = i + 1; j < peopleBinary.length; j++) {
                if (peopleBinary[i] == peopleBinary[j]) isDuplicateArr[j] = true;
                else if (peopleBinary[i] > peopleBinary[j] && isPeopleContainsAll(peopleBinary[i], peopleBinary[j], len)) {
                    isDuplicateArr[j] = true;
                } else if (peopleBinary[j] > peopleBinary[i] && isPeopleContainsAll(peopleBinary[j], peopleBinary[i], len)) {
                    isDuplicateArr[i] = true;
                }
            }
        }
        return isDuplicateArr;
    }

    private boolean isPeopleContainsAll(int skill1, int skill2, int len) {
        int mask = 1;
        for (int i = 0; i < len; i++) {
            if ((skill1 & mask) == 0 && (skill2 & mask) > 0) return false;
            mask <<= 1;
        }
        return true;
    }
}

// DP
class Solution {
    public int[] smallestSufficientTeam(String[] req_skills, List<List<String>> people) {
        int target = (1 << req_skills.length) - 1;
        Map<String, Integer> skill_map = new HashMap<>();
        for(int i = 0; i < req_skills.length; i++) skill_map.put(req_skills[i], 1 << i);

        int[] skills = new int[people.size()];
        for(int i = 0; i < people.size(); i++) {
            int mask = 0;
            for(String s : people.get(i)) {
                mask |= skill_map.get(s);
            }
            skills[i] = mask;
        }

        int[] dp = new int[1 << req_skills.length];
        int[][] pt = new int[1 << req_skills.length][2];

        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;

        for(int i = 0; i < people.size(); i++) {
            int k = skills[i];
            if(k == 0) continue;
            for(int j = target; j >= 0; j--) {
                if(dp[j] + 1 < dp[j | k]) {
                    dp[j | k] = dp[j] + 1;
                    pt[j | k] = new int[] {j, i};
                }
            }
        }

        List<Integer> ret = new ArrayList<>();
        while(target != 0) {
            ret.add(pt[target][1]);
            target = pt[target][0];
        }
        return ret.stream().mapToInt(i->i).toArray();
    }
}