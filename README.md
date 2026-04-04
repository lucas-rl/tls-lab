# TLS Learning Lab Plan

## Purpose of this Lab
- Build a deep, practical understanding of how network communication works from HTTP up to TLS and mTLS
- Explore core problems:
  - Lack of confidentiality (data can be read)
  - Lack of integrity (data can be modified)
  - Lack of authentication (no guarantee of identity)
- Reproduce real-world attacks such as Man-in-the-Middle (MITM)
- Incrementally design and implement solutions to these problems
- Understand how modern security (TLS) solves these issues in a standardized way
- Gain the ability to:
  - Debug TLS and certificate issues
  - Understand client/server authentication flows
  - Reason about security in distributed systems

---

## Phase 1 — Plain HTTP
- Build client and server using raw Java sockets
- Send and read HTTP requests manually
- Observe HTTP as plain text over TCP
- Insight: no encryption, no security

## Phase 2 — Proxy (MITM)
- Build a proxy between client and server
- Log, forward, and modify requests/responses
- Insight: traffic can be intercepted and altered without detection

## Phase 3 — Fake Sniffer (TCP level)
- Read raw TCP streams instead of structured HTTP
- Parse requests manually (\r\n\r\n, headers, body)
- Insight: TCP is stream-based, parsing is non-trivial

## Phase 4 — Toy Crypto (encryption only)
- Add simple encryption (XOR or AES)
- Encrypt on client, decrypt on server
- Insight: encryption hides data but does not guarantee security

## Phase 5 — Break Encryption (MITM)
- Intercept encrypted traffic via proxy
- Modify or replay encrypted data
- Insight: encryption without authentication is vulnerable

## Phase 6 — Identity (public/private keys)
- Introduce asymmetric crypto (RSA)
- Exchange keys and establish shared secret
- Insight: enables secure key exchange

## Phase 7 — Break Identity (no trust)
- Attacker replaces server public key
- Client trusts fake identity
- Insight: identity without trust is useless

## Phase 8 — Certificate Authority (CA)
- Create your own CA
- Sign server certificates
- Client verifies certificate signature
- Insight: trust chain prevents MITM

## Phase 9 — Real TLS (Java)
- Use SSLServerSocket, KeyStore, TrustStore
- Implement HTTPS communication
- Test valid/invalid certificates
- Insight: TLS automates all previous steps

## Phase 10 — mTLS
- Require client certificates on server
- Validate both client and server identities
- Insight: mutual authentication (real-world systems)

---

## Suggested Daily Plan (10–15 days)

### Day 1
- Implement basic HTTP server (ServerSocket)
- Send simple GET request from client
- Log raw request

### Day 2
- Add POST support
- Parse headers and body
- Experiment with malformed requests

### Day 3
- Build basic proxy
- Forward requests from client → server
- Log full request/response

### Day 4
- Modify requests in proxy (headers, path, body)
- Simulate MITM attack

### Day 5
- Move to raw TCP parsing
- Manually detect request boundaries
- Handle partial reads

### Day 6
- Improve parser robustness
- Break parsing intentionally and fix

### Day 7
- Add simple encryption (XOR or AES)
- Encrypt client → decrypt server

### Day 8
- Try intercepting encrypted traffic
- Modify payload blindly
- Observe failures

### Day 9
- Implement RSA key exchange
- Securely share a symmetric key

### Day 10
- Perform MITM by replacing public key
- Observe how identity is broken

### Day 11
- Create your own CA (conceptually or via keytool/openssl)
- Sign server certificate

### Day 12
- Validate certificates on client
- Reject invalid or fake certificates

### Day 13
- Replace system with real Java TLS (SSLServerSocket)
- Run HTTPS server

### Day 14
- Break TLS (wrong cert, untrusted CA)
- Debug handshake failures

### Day 15
- Implement mTLS
- Require client certificates
- Test valid vs invalid clients

---

## Final Outcome
- Understand HTTP, TCP, TLS, and mTLS deeply
- Be able to simulate attacks and defenses
- Understand certificates, trust, and encryption in practice
