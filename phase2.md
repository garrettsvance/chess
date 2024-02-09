[Phase 2 Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAEooDmSAzmFMARDQVqhFHXyFiwUgBF+wAIIgQKLl0wATeQCNgXFDA3bMmdlAgBXbDADEaYFQCerDt178kg2wHcAFkjAxRFRSAFoAPnJKGigALhgAbQAFAHkyABUAXRgAegt9KAAdNABvfMp7AFsUABoYXDVvaA06lErgJAQAX0xhGJgIl04ePgEhaNF4qFceSgAKcqgq2vq9LiaoFpg2joQASkw2YfcxvtEByLkwRWVVLnj2FDAAVQKFguWDq5uVNQvDbTxMgAUQAMsC4OkYItljAAGbmSrQgqYb5KX5cAaDI5uUaecYiFTxNAWBAIQ4zE74s4qf5o25qeIgab8FCveYw4DVOoNdbNL7ydF3f5GeIASQAciCWFDOdzVo1mq12p0YJL0ilkbQcSMPIIaQZBvSMUyWYEFBYwL53hUuSgBdchX9BqK1VLgTKtUs7XVgJbfOkIABrdBujUwP1W1GChmY0LYyl4-UTIkR-2BkNoCnHJMEqjneORPqUeKRgPB9C9aKULGRYLoMDxABMAAYW8USmWM+geugNCYzJZrDZoNJHjBQRBOGgfP5Aph62Ei9W4olUhlsjl9Gp8R25SteRsND1i7AIjqqcnCSh4ggp0g0DaoBKfQq+ZsDhfcwaLsa7rE4QfDR2SgJ8X2qB0fjuCJXRBcFIS9cCDAAMRYFIAFkvWjR1Y0LL89TzSYYC4CxY2zXECINCI-0ZR4wCSNYjzAu1IKdLgYMBcgwQhKFD2aGBUIwrCaPY89E0olMb2I0iMXI3VTkk6iYxNGBmRQVlu0fRYkNY3DwldSVpVlAokN9dMKxndVNTLbCoLUPDxIU69S3MzM5MvQiVELU8XKtTSq2oShC0XRsYFbFsYDKEzXy7CzezQftTHMKxbDMFBQ0ndhLGYGw-ACIJkAbf4fMSGRuPSYFN23Lhd3sVzK1PMScwk5yYDvLLLTmWLM0-RzqUU8IRKmNoIBoTSuvqtBdIxDj4jK8EKrTPyLIEtDMJskSHOapz81TEiyPwnbRG8ld4n22TGoiELmzbTA+wHZLh2mDQJzcGAAHE7UxXK5wKkJmEGEqEne4Eslydg7WKbqGpXJqKKO1NkB4T7qi4Cblp69zvwGob4SA8boam2y2Nmrj4KhQnVqEjblOguH5P65zzruLGWt2lAlJwlSkbAFHVDmaboP0zi4J4mAIdRqnMIl1Ridw+mPINB4voSB84QgTJWYRjmIhKmWuFVtB1c1y7wmusK22KfWEhlsUZDqPLAhA5Y6m0BBQCDZ3XxlpDNbuhKHqHWxsAsKBsAQAw4DNAw+dnfKF0KpcokC1dkjSMGchlqHJo7H27RPWHwkOxn2dNdTAj59Hy0zOo84grWS+OwbacZPGEoJybBfs4WgW4hDKcE9b-TlmaFexpmZJZ4ur3Zzm7PuNTWUruv7RHoXRUMj0oRXsNNX1tf7LHtmiNtmQG5n47ddO8W7TtgKYmCxPG3Cq3b7P+6kqDmxHBQMkIG8GAAApCAD4Pp2lsG7D2Cd-rFWvskZ4Gcs51QxugDsIU4AQDvFAWub8C4pyPtreIAArEBaBK6E16ttRuXlm5c3-G3DQHcUFE02j3MmYsB5rSWr4A+oki59QvkSZmahz6eQ5rQ+eAEgLLzfl3dibDRYIVPlLG+qNeFbXhtQqSwj1DTzEXPNixDSHkMmjg6ods5Gk03p6SmVlVEoDtjAUB70FDoWBGQdRBCtFnUniIvRVEr4px8WRU25twr+0SoOFKNgzDAGcIgdSsBgDYHDoQAicd5whVgUE0q5VKq5CMPfIK-CqGCKkiACOeABaiKohIwxqlKmgUsWw+aoNgQRjJCommdDu6ulaYtYAnTB7CRbvIvpeSOkIBUfvTaXiynBNkv4xSgSYgLJZqEp+N0WwRMwEAA)

## Raw:
actor Client
participant Server
participant RegistrationService
participant DataAccess
database db

group #navy Registration #white
Client -> Server: [POST] /user\n{username, password, email}
Server -> RegistrationService: register(username, password, email)
RegistrationService -> DataAccess: getUser(username)
DataAccess -> db: SELECT username from user
DataAccess --> RegistrationService: null
RegistrationService -> DataAccess: createUser(username, password)
DataAccess -> db: INSERT username, password, email INTO user
RegistrationService -> DataAccess: createAuth(username)
DataAccess -> db: INSERT username, authToken INTO auth
DataAccess --> RegistrationService: authToken
RegistrationService --> Server: authToken
Server --> Client: 200\n{authToken}
end

group #orange Login #white
Client -> Server: [POST] /session\n{username, password}
Server ->RegistrationService: login(userName, password)
RegistrationService ->DataAccess:findUser(userName)
DataAccess->db: SELECT userName FROM user
DataAccess-->RegistrationService: success
RegistrationService->DataAccess:getPassword(userName)
DataAccess->db: SELECT password FROM user
DataAccess-->RegistrationService: success
RegistrationService->DataAccess: createToken(userName)
DataAccess->db: INSERT userName, authToken INTO auth
DataAccess-->RegistrationService: authToken
RegistrationService-->Server: authToken
Server-->Client: 200 {userName, authToken}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server->RegistrationService: logout(authToken)
RegistrationService->DataAccess: removeToken(authToken)
DataAccess->db: DELETE authToken FROM auth
DataAccess-->RegistrationService: success
RegistrationService-->Server: success
Server-->Client: 200
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server->RegistrationService: listGames(authToken)
RegistrationService->DataAccess: findToken(authToken)
DataAccess->db: SELECT authToken FROM auth
DataAccess-->RegistrationService:success
RegistrationService->DataAccess: listGames()
DataAccess->db: SELECT games FROM games
DataAccess-->RegistrationService: games[info]
RegistrationService-->Server: games[info]
Server-->Client: 200\ngames[gameID, whiteUsername, blackUsername, gameName]

end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{gameName}
Server->RegistrationService: createGame(authToken, gameName)
RegistrationService->DataAccess: findToken(authToken)
DataAccess->db: SELECT authToken FROM auth
DataAccess-->RegistrationService:success
RegistrationService->DataAccess:createGame(gameName)
DataAccess->db:INSERT gameName INTO games
DataAccess-->RegistrationService: gameID
RegistrationService-->Server: gameID
Server-->Client:200\ngameID
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{ClientColor, gameID}
Server->RegistrationService: joinGame(authToken)
RegistrationService->DataAccess: findToken(authToken)
DataAccess->db: SELECT authToken FROM auth
DataAccess-->RegistrationService:success
RegistrationService->DataAccess:findGame(gameID)
DataAccess->db: SELECT gameID FROM games
DataAccess-->RegistrationService: success
RegistrationService->DataAccess: joinGame(authToken, gameID)
DataAccess->db: INSERT authToken INTO gameID in GAMES
DataAccess-->RegistrationService: success
RegistrationService-->Server: success
Server-->Client: 200
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server->RegistrationService: clear()
RegistrationService->DataAccess: clear()
DataAccess->db: DELETE all FROM auth
DataAccess->db: DELETE all FROM user
DataAccess->db: DELETE all FROM games
DataAccess-->RegistrationService: success
RegistrationService-->Server: success
Server-->Client: 200
end
