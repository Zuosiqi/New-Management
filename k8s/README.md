# Kubernetes deployment notes

1. Build images from the repository root:

```powershell
.\scripts\build-images.ps1 -RegistryPrefix ea-management -Tag 1.0 -ContainerCli nerdctl
```

2. If you do not use a remote registry, save and import every image on all three nodes.

3. The manifests assume:
   - namespace: `ea-ms`
   - MySQL Service DNS: `mysql.default`
   - MySQL username/password: `root/root`

4. Apply manifests in order:

```bash
kubectl apply -f k8s/00-namespace-and-secret.yaml
kubectl apply -f k8s/01-eureka.yaml
kubectl apply -f k8s/02-employee.yaml
kubectl apply -f k8s/03-attendance.yaml
kubectl apply -f k8s/04-leave.yaml
kubectl apply -f k8s/05-gateway.yaml
```

5. External access:
   - Eureka: `http://<master-ip>:30061`
   - Gateway: `http://<master-ip>:30099`

6. Gateway requests must carry `?token=1`, for example:

```text
http://192.168.79.133:30099/employee/test?token=1
http://192.168.79.133:30099/attendance/test?token=1
http://192.168.79.133:30099/leave/test?token=1
```
