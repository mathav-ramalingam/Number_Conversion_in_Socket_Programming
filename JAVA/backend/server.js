const express = require('express');
const net = require('net');
const dgram = require('dgram');
const bodyParser = require('body-parser');
const cors = require('cors'); // CORS middleware

const app = express();
app.use(cors()); // Enable CORS
app.use(bodyParser.json());

const TCP_SERVER_HOST = '127.0.0.1';
const TCP_SERVER_PORT = 5000;

const UDP_SERVER_HOST = '127.0.0.1';
const UDP_SERVER_PORT = 5001;

// Endpoint for conversion
app.post('/convert', (req, res) => {
    const { protocol, number, inputBase, outputBase } = req.body;

    if (!number || !inputBase || !protocol) {
        return res.status(400).send('Missing required parameters: number, inputBase, and protocol.');
    }

    const message = `${number} ${inputBase}`;

    if (protocol === 'TCP') {
        const tcpClient = new net.Socket();

        tcpClient.connect(TCP_SERVER_PORT, TCP_SERVER_HOST, () => {
            tcpClient.write(message + '\n');
        });

        tcpClient.on('data', (data) => {
            // Assuming the TCP server will return the converted result
            res.json({ converted: data.toString().trim() });
            tcpClient.destroy();
        });

        tcpClient.on('error', (err) => {
            res.status(500).send('Error connecting to TCP server');
            console.error('TCP Client error:', err.message);
            tcpClient.destroy();
        });

    } else if (protocol === 'UDP') {
        const udpClient = dgram.createSocket('udp4');

        udpClient.send(message, UDP_SERVER_PORT, UDP_SERVER_HOST, (err) => {
            if (err) {
                res.status(500).send('Error sending UDP message');
                udpClient.close();
                return;
            }
        });

        udpClient.on('message', (msg) => {
            res.json({ converted: msg.toString().trim() });
            udpClient.close();
        });

        udpClient.on('error', (err) => {
            res.status(500).send('Error connecting to UDP server');
            console.error('UDP Client error:', err.message);
            udpClient.close();
        });
    } else {
        return res.status(400).send('Invalid protocol. Please choose TCP or UDP.');
    }
});

// app.listen(3000, () => {
//     console.log('HTTP server listening on port 3000');
// });
app.listen(3000, () => {
    console.log('HTTP server listening on port 3000');
});