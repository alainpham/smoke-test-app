import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter } from 'k6/metrics';

// A simple counter for http requests

export const requests = new Counter('http_reqs');

// you can specify stages of your test (ramp up/down patterns) through the options object
// target is the number of VUs you are aiming for

export const options = {
    stages: [
        { target: 5, duration: '1m' },
        { target: 25, duration: '1m' },
        { target: 10, duration: '1m' },
    ],
    thresholds: {
        http_reqs: ['count < 100'],
    },
};

export default function () {
    // our HTTP request, note that we are saving the response to res, which can be accessed later
    const url = 'http://localhost:8090';

    response = http.get(url + "/ping");
    
    sleep(0.3);
    
    response = http.get(url + "/ping");
    
    sleep(0.2);
    
    response = http.post(
        url + '+/send-msg',
        '{"id":"123","name":"John","firstName":"Doe","vote":"yes","text-area":""}'
    );
    
    sleep(0.5);

    response = http.get(url + "/camel-openapi");
    sleep(0.4);

    response = http.get(url + "/restsvc/ping");

    
}
