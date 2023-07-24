#! /bin/sh
#Store CLI arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#Check # of args
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

#Store lscpu and hostname
lscpu_out=`lscpu`
hostname=$(hostname -f)

##Retrieve hardware data
cpu_number=$(echo "$lscpu_out"  | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_arch=$(echo "$lscpu_out"  | egrep "^Architecture" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"  | egrep "^Model name:"|awk -F':' '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_out"  | egrep "^CPU MHz:" | awk '{print $3}' | tr -d 'K' | xargs)
l2_cache=$(echo "$lscpu_out"  | egrep "^L2 cache:" | awk '{print $3}'| tr -d 'K' | xargs)
timestamp="$(date +"%Y-%m-%d %H:%M:%S")"
mem_total=$(cat /proc/meminfo | egrep "^MemTotal:" | awk '{ print $2}' | xargs)

#SQL command to insert a new row to host_info table
insert_smt="INSERT INTO host_info(hostname,cpu_number, cpu_architecture , cpu_model, cpu_mhz,l2_cache,timestamp,total_mem) VALUES('$hostname', $cpu_number, '$cpu_arch','$cpu_model', $cpu_mhz,$l2_cache, '$timestamp',$total_mem)";

export PGPASSWORD=$psql_password

#Run the insert line
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_smt"
exit $?