[![Release](https://jitpack.io/v/https://jitpack.io/v/helton-isac/SimpleBiometricAPI.svg)]
(https://jitpack.io/#helton-isac/SimpleBiometricAPI)

# SimpleBiometricAPI

API criada como parte do trabalho de conclusão de curso, baseada no code lab de biometrics do google, porém transformada em uma LIB: 

https://developer.android.com/codelabs/biometric-login#0

Exemplo de como utilizar:

Verificar se o device possui sensores biometricos compativeis:
```
BiometricsApi().canAuthenticate(context)
```


Exibir o dialog de biometria para gravar o token pela primeira vez: 
```
BiometricsApi().showBiometricPromptForEncryption(
    activityContext = context,
    title = title,
    subtitle = subtitle,
    negativeButtonText = negativeButtonText,
    token = token,
    onAuthenticationSucceeded = {
      // TODO: your callback
    },
    onAuthenticationError = {
      // TODO: your callback
    },
    onAuthenticationFailed = {
      // TODO: your callback
    }
)
```

Exibir o dialog de biometria para recuperar o token: 
```
BiometricsApi().showBiometricPromptForDecryption(
    activityContext = context,
    title = title,
    subtitle = subtitle,
    negativeButtonText = negativeButtonText,
    onAuthenticationSucceeded = { token ->
      // TODO: your callback, token is passed as parameter
    },
    onAuthenticationError = {
      // TODO: your callback
    },
    onAuthenticationFailed = {
      // TODO: your callback
    }
)
```
