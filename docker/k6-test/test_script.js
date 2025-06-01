import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data';
import {scenario} from 'k6/execution';
import {htmlReport} from "https://raw.githubusercontent.com/benc-uk/k6-reporter/2.4.0/dist/bundle.js";

// CSV 데이터 로드
const users = new SharedArray("users", function () {
    return open('./users.csv')
        .split('\n')
        .map(row => {
            const [provider, providerId, name, email, picture] = row.split(',');
            return {provider, providerId, name, email, picture};
        });
});

export const options = {
    scenarios: {
        constant_rate_test: {
            executor: 'constant-arrival-rate',
            rate: 100,               // 초당 요청 수
            timeUnit: '1s',          // 요청 속도 단위
            duration: '10s',         // 전체 실행 시간
            preAllocatedVUs: 1000,    // 미리 할당할 가상 사용자 수
            maxVUs: 1000,            // 최대 가상 사용자 수
        }
    }
};

export default function () {
    const index = scenario.iterationInTest;
    if (index >= users.length) {
        console.warn(`Index ${index} is out of users array bounds, skipping iteration.`);
        return;
    }

    const user = users[index];

    // 1️⃣ 회원가입
    const headers = {'Content-Type': 'application/json'};
    const signUpResponse = http.post('http://host.docker.internal:8082/api/v1/members', JSON.stringify(user), {headers: headers});
    check(signUpResponse, {
        '회원가입 성공': (response) => response.status === 200,
    });

    const responseBody = signUpResponse.json();
    const id = responseBody.id || responseBody.id || null;
    if (!id) {
        console.error(`❌ 회원가입 실패, 응답: ${signUpResponse.body}`);
        return;
    }

    // 📌 헤더에 userId 추가
    const customHeaders = {
        'Content-Type': 'application/json',
        'X-User-Id': id,
    };

    // 2️⃣ 회원 정보 조회
    const getMemberResponse = http.get('http://host.docker.internal:8082/api/v1/members/me', {headers: customHeaders});
    check(getMemberResponse, {
        '회원 정보 조회 성공': (response) => response.status === 200,
    });

    // 3️⃣ 회원 프로필 조회
    const getProfileResponse = http.get('http://host.docker.internal:8082/api/v1/members/me/profile', {headers: customHeaders});
    check(getProfileResponse, {
        '회원 프로필 조회 성공': (response) => response.status === 200,
    });

    sleep(0.1); // 부하 조절ª
}

export function handleSummary(data) {
    return {
        "summary.html": htmlReport(data),
    };
}
