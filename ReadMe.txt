Vault init keys:

Unseal Key 1: haBQesVAh6HdmklKQGSbvSjZx4/Z1ZapT3xkky7LDooB
Unseal Key 2: kNEEovRXhiN6vEyS9w6JE7h7JO5M0Jv+R7+wUT1QFHwC
Unseal Key 3: AifyrABeYABcDM/I8i/xYa1aTdTykj8JjrLkBy7711sD
Unseal Key 4: 1Ji2xnhFdGr7hIfX7PdpjKbM2r8aOL5KJIuLFqXvb14E
Unseal Key 5: Rm5AyIxMkkndNASN6dYR/rPts4Wkehq97YbfQLZErHkF
Initial Root Token: 78600c52-5062-1d55-7e50-6b88c8865e79
*This is not real keys, just an example of how they look


======= example.hcl ==============
disable_mlock = true

backend "mysql" {
  address = "localhost:3306"
  username = "root"
  password = ""
  path = "vault"
}

listener "tcp" {
 address = "127.0.0.1:8200"
 tls_disable = 1
}
==================================

1. Starta servern med: vault server -config=example.hcl

2. Öppna en ny bash för att börja arbeta med vault klienten.

3. Skriv: ”export VAULT_ADDR='http://127.0.0.1:8200’”, vilket sätter var klienten skall skicka sina requests

4. Skriv: ”export VAULT_TOKEN="78600c52-5062-1d55-7e50-6b88c8865e79”, vilket sätter token ovan som klienten använder

5. Första gången du startar vault så måste du göra ”vault init” vilket producerar nycklar och token ovan. Dessa kommer bara visas en enda gång så spara undan dom på något bra och hemligt ställe.

6. Efter att ha startat Vault server så behöver man låsa upp den med minst 3 av ovan ”unseal”-keys. Detta sker genom att man skriver: ”vault operator unseal” och anger första nyckeln, sedan gör man om förfarandet och anger nästa nyckel, 2ggr till. Detta kan ju bli en kul grej att sätta upp i ett /etc/init.d script. (går oxå att göra detta via rest-api.

7. För att skicka en hemlighet till vault: ”vault write secret/hello value=world”

8. För att läsa denna hemlighet från vault: ”vault read secret/hello”

obs i rest-api:et måste man skicka med det token man fått tillbaka från authensieringen vid varje request.

Vi vill använda appRole som autentiseringsbackend, på detta sätt kan vi knyta entitlements som vi sätter upp i sukat till behörighet i vault. Vi måste även skapa eller använda policies knutna till dessa roller.
1. Efter du startat och initierat vault skriv: ”vault auth-enable approle”
2. Skapa eller tänk ut vilka policies som ska matcha rollen/entitlement, skriv: ”vault policy-write testgroup policy.hcl” där policy.hcl är en txt-fil med innehållet:
====== policy.hcl ========
path "secret/*" {
  policy = "write"
}
==========================
3. För att skapa rollen/entitlement ”testgroup” och använda policy ”testgroup” skriv: ”vault write -f auth/approle/role/testgroup policies="testgroup"”.
4. Nu börjar själva loginförfarandet.
5. Hämta role_id med root token, skriv: ”vault read auth/approle/role/testgroup/role-id” 
6. Hämta secret_id, skriv: ”vault write -f auth/approle/role/testgroup/secret-id”
7. Logga in, skriv: ”vault write auth/approle/login role_id=<role_id från 4> secret_id=<secret_id från 5>”
8. Notera ”token” från 6, notera ”token_duration” från 6.
9. Nu kan du sätta det nya token enligt punk 3 i första avdelningen. Det är också detta token som ska användas i rest-api:et.


Andra viktiga förberedelser:
1. Användarna för denna app måste skaffa sig rätt entitlement i sukat.
2. Lägg till en urn: urn:mace:swami.se:gmai:su-vaulttool:<appRole>
3. <appRole> är den roll/grupp/entitlement som man vill att denna användare skall tillhöra, exempelvis ”testgroup” som används i tidigare exempel i detta dokument.
4. Sedan behöver man antingen sätta upp en Apache framför denna web-app som är shibbad eller gå via någon shibproxy.
5. Glöm heller inte att sätta upp den externa configurationsfilen /local/vaulttool/conf/application.yml med vault-entries för root-token och url till vault.
6. Denna app körs under Java 1.8 och startas med: java -jar build/libs/vaulttool.jar


