# azure-blob-storage-intro
Enkelt eksempel på FTP-liknende operasjoner mot Azure Blob Storage.

Forhåndskrav:
* Maven og Java 14
* Azure-konto
* Azure CLI installert (kommandoen `az`)

## Bygging
`$ mvn package`

Dette bygger også en FAT-jar med klassifikatoren `all` for å forenkle 
kommandolinje-kjøring under.


## Kjøring

### Opprett en resource group i Azure (hvis du ikke har en fra før av)
`az group create --name  <resource-group> --location <location>`

List lokasjer med `$ az account list-locations -o table`

### Opprett en storage account:
```
$ az storage account create \
        --name <storage-account> \
        --resource-group <resource-group> \
        --location <location> \
        --sku Standard_LRS \
        --https-only
```

### Hent ut credentials
#### Connection String

```
$ az storage account show-connection-string --name svejk -o tsv
DefaultEndpointsProtocol=https;EndpointSuffix=core.windows.net;AccountName=svejk;AccountKey=...==
```
Tilordne resultatet til en miljøvariabel med navnet `AZURE_STORAGE_CONNECTION_STRIN`.
Sett også miljøvariabelen `AZURE_STORAGE_ACCOUNT_NAME`.

### Opprette en container
```
$ java -jar target/azure-blob-storage-intro-0.0.1-SNAPSHOT-all.jar  mkdir container
```

### Fjerne en container
```
$ java -jar target/azure-blob-storage-intro-0.0.1-SNAPSHOT-all.jar rmdir container
```

### Laste opp en fil
```
$ java -jar target/azure-blob-storage-intro-0.0.1-SNAPSHOT-all.jar put container src/main/java/com/example/azure/blob/Btp.java
```

### Hente ut en fil
```
$ java -jar target/azure-blob-storage-intro-0.0.1-SNAPSHOT-all.jar get container src/main/java/com/example/azure/blob/Btp.java dump.txt
```

### Liste ut filer i en container
```
$ java -jar target/azure-blob-storage-intro-0.0.1-SNAPSHOT-all.jar ls container
```

### Eksperimentere med autorisering (--auth-mode)
For å eksperimentere med de forskjellige autoriseringsmekanismene, hent ut hemmelighet med az-kommandoene listet nedenfor, sett korresponderende miljøvariable og velg auth-mode for formålet.

#### connection-string
Se over. 

#### account-key
Miljøvariable:
`AZURE_STORAGE_ACCOUNT_NAME`
`AZURE_STORAGE_ACCOUNT_KEY`
Verdien av sistnevnte kan lese ut fra key1 i portalen.
auth-mode: `account-key`


#### SAS Token
1.  Generer ett SAS-token for lagringskontoen med en az-kommando á la:
Miljøvariabel: `AZURE_STORAGE_SAS`
auth-mode: `sas-token`
```
 $ az storage account generate-sas --account-name svejk --services b --https-only --resource-types sc --expiry 2020-09-02 --permissions acdlpruw -o tsv
 ```
#### Azure AD (`login`)
Miljøvariable:
AZURE_TENANT_ID
AZURE_CLIENT_ID
AZURE_CLIENT_SECRET

auth-mode: `login`
1. Opprett en tjenestebruker med rollen "Storage Blob Data Contributor"
```
$ az ad sp create-for-rbac --name storage-test --role "Storage Blob Data Contributor" --scopes //subscriptions/768fe5e1-ab77-4fce-8551-7c2a98526c9d/resourceGroups/storage-resource-group/providers/Microsoft.Storage/storageAccounts/svejk
```
Kopier verdiene fra JSON-responsen til miljøvariablene. Merk at `password` er `AZURE_CLIENT_SECRET`.


### Opprydding i Azure

`$ az group delete --name <resource-group> --yes`

Hvis du testet en service principal i Azure AD:
```
$ az ad sp delete --id $AZURE_CLIENT_ID
```








